package com.milktea.backend.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.milktea.backend.config.AlipayConfig;
import com.milktea.backend.dto.*;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.*;
import com.milktea.milktea_backend.model.entity.Order;
import com.milktea.milktea_backend.model.entity.OrderItem;
import com.milktea.milktea_backend.model.entity.User;
import com.milktea.milktea_backend.model.entity.Store;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.OrderStatusTimeline;
import com.milktea.milktea_backend.model.entity.PointTransaction;
import lombok.RequiredArgsConstructor;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final OrderStatusTimelineRepository timelineRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final MemberLevelRepository memberLevelRepository;
    private final OrderReviewRepository orderReviewRepository;
    private final ComplaintRepository complaintRepository;
    private final UserCouponRepository userCouponRepository;
    private final AddressService addressService;
    private final CouponService couponService;
    private final PromotionRepository promotionRepository;
    private final OrderAppealRepository orderAppealRepository;
    private final org.springframework.context.ApplicationContext applicationContext;

    @Transactional
    public Order createOrderFromDTO(Long userId, OrderCreateDTO dto) {
        if (dto.getStoreId() == null) {
            throw new ServiceException("INVALID_PARAMETER", "门店ID不能为空");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("USER_NOT_FOUND", "用户不存在"));
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new ServiceException("STORE_NOT_FOUND", "店铺不存在"));

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setStore(store);
        order.setStatus("PENDING_PAYMENT");
        order.setDeliveryType(dto.getDeliveryType());
        order.setAddressJson(dto.getAddressJson());
        order.setRemark(dto.getRemark());
        order.setOrderTime(LocalDateTime.now());
        order.setIsCommented(false);
        order.setRemindCount(0);
        order.setBalanceDiscountAmount(dto.getBalanceDiscountAmount() != null ? dto.getBalanceDiscountAmount() : BigDecimal.ZERO);

        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            if (itemDto.getProductId() == null) {
                throw new ServiceException("INVALID_PARAMETER", "商品ID不能为空");
            }
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ServiceException("PRODUCT_NOT_FOUND", "商品不存在"));
            
            // 优先使用前端传递的价格，若无则使用商品原价
            BigDecimal unitPrice = itemDto.getPrice();
            if (unitPrice == null) {
                unitPrice = product.getPrice();
            }
            if (unitPrice == null) {
                throw new ServiceException("INVALID_PARAMETER", "商品价格异常");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setProductImage(product.getImageUrl());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(unitPrice);
            item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            return item;
        }).collect(Collectors.toList());

        order.setOrderItems(items);
        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // 设置配送费
        BigDecimal deliveryFee = BigDecimal.ZERO;
        if ("DELIVERY".equals(dto.getDeliveryType())) {
            // 简单逻辑：满30免配送费，否则5元。实际应根据前端计算逻辑保持一致
            deliveryFee = totalAmount.compareTo(new BigDecimal("30")) >= 0 ? BigDecimal.ZERO : new BigDecimal("5");
        }
        order.setDeliveryFee(deliveryFee);
        
        // 计算折扣和实付
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        // 1. 会员折扣
        if (user.getMemberLevel() != null) {
            BigDecimal rate = user.getMemberLevel().getDiscountRate();
            if (rate != null && rate.compareTo(BigDecimal.ONE) < 0) {
                BigDecimal discountedTotal = totalAmount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
                discountAmount = totalAmount.subtract(discountedTotal);
            }
        }
        
        // 2. 积分抵现
        if (dto.getUsePoints() != null && dto.getUsePoints() > 0) {
            if (user.getPoints() < dto.getUsePoints()) {
                throw new ServiceException("POINTS_NOT_ENOUGH", "积分不足");
            }
            // 假设100积分抵1元
            BigDecimal pointsValue = BigDecimal.valueOf(dto.getUsePoints()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
            discountAmount = discountAmount.add(pointsValue);
            
            // 扣除积分
            user.setPoints(user.getPoints() - dto.getUsePoints());
            userRepository.save(user);
            
            // 记录积分流水
            PointTransaction pt = new PointTransaction();
            pt.setUser(user);
            pt.setAmount(-dto.getUsePoints());
            pt.setBalanceAfter(user.getPoints());
            pt.setType("SPEND");
            pt.setRemark("订单抵现: " + order.getOrderNo());
            pointTransactionRepository.save(pt);
        }

        // 3. 促销活动 (满减、第二杯半价、限时折扣)
        LocalDateTime now = LocalDateTime.now();
        List<com.milktea.milktea_backend.model.entity.Promotion> activePromotions = promotionRepository.findAll().stream()
                .filter(p -> p.getIsActive() != null && p.getIsActive() && p.getStartTime().isBefore(now) && p.getEndTime().isAfter(now))
                .collect(Collectors.toList());

        for (com.milktea.milktea_backend.model.entity.Promotion p : activePromotions) {
            try {
                Map<String, Object> rules = new com.fasterxml.jackson.databind.ObjectMapper().readValue(p.getRulesJson(), Map.class);
                if ("FULL_REDUCE".equals(p.getType())) {
                    BigDecimal threshold = new BigDecimal(rules.get("threshold").toString());
                    BigDecimal reduce = new BigDecimal(rules.get("reduce").toString());
                    if (totalAmount.compareTo(threshold) >= 0) {
                        discountAmount = discountAmount.add(reduce);
                    }
                } else if ("SECOND_HALF".equals(p.getType())) {
                    // 简单逻辑：如果商品数量 >= 2，减去最便宜的一件的一半价格
                    if (items.size() >= 2 || (items.size() == 1 && items.get(0).getQuantity() >= 2)) {
                        BigDecimal minPrice = items.stream().map(OrderItem::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                        discountAmount = discountAmount.add(minPrice.multiply(new BigDecimal("0.5")));
                    }
                } else if ("LIMITED_DISCOUNT".equals(p.getType())) {
                    BigDecimal rate = new BigDecimal(rules.get("rate").toString()); // 如 0.8
                    BigDecimal promoDiscount = totalAmount.multiply(BigDecimal.ONE.subtract(rate));
                    discountAmount = discountAmount.add(promoDiscount);
                }
            } catch (Exception e) {
                // 规则解析失败，跳过该活动
            }
        }

        order.setDiscountAmount(discountAmount);
        BigDecimal actualAmount = totalAmount.subtract(discountAmount);
        if (actualAmount.compareTo(BigDecimal.ZERO) < 0) actualAmount = BigDecimal.ZERO;
        order.setActualAmount(actualAmount);

        // 保存地址到历史记录
        if ("DELIVERY".equals(order.getDeliveryType())) {
            addressService.saveToHistory(user, order.getAddressJson());
        }

        Order savedOrder = orderRepository.save(order);
        saveTimeline(savedOrder, "PENDING_PAYMENT");
        return savedOrder;
    }

    private void saveTimeline(Order order, String status) {
        OrderStatusTimeline timeline = new OrderStatusTimeline();
        timeline.setOrder(order);
        timeline.setStatus(status);
        timelineRepository.save(timeline);
    }

    @Transactional
    public Order updateOrderRemark(String orderNo, String remark) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setRemark(remark);
        return orderRepository.save(order);
    }

    @Transactional
    public void payOrder(String orderNo, String payMethod) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setPayMethod(payMethod);
        orderRepository.save(order);
        updateOrderStatusByNo(orderNo, "PAID");
    }

    @Transactional
    public void updateOrderStatusByNo(String orderNo, String status) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        String oldStatus = order.getStatus();
        order.setStatus(status);
        if ("PAID".equals(status) && !"PAID".equals(oldStatus)) {
            order.setPayTime(LocalDateTime.now());
            order.setQueueNumber(new Random().nextInt(100) + 1);
            order.setEstimatedReadyTime(LocalDateTime.now().plusMinutes(15));

            User user = order.getUser();
            // 钱包支付扣款逻辑：仅当支付方式为 BALANCE 时扣除余额
            if ("BALANCE".equalsIgnoreCase(order.getPayMethod())) {
                BigDecimal actualAmount = order.getActualAmount() != null ? order.getActualAmount() : BigDecimal.ZERO;
                if (user.getBalance() != null && user.getBalance().compareTo(actualAmount) >= 0) {
                    user.setBalance(user.getBalance().subtract(actualAmount));
                    userRepository.save(user);
                } else {
                    throw new ServiceException("BALANCE_NOT_ENOUGH", "余额不足");
                }
            }
            
            // 14. 模拟排队逻辑：零回归原则，使用独立线程异步处理
            try {
                simulateOrderQueue(order);
            } catch (Exception e) {
                // 模拟逻辑异常不影响主支付流程
            }

            // 支付成功，增加积分 (1元 = 1积分)
            try {
                int earnPoints = order.getActualAmount().intValue();
                if (earnPoints > 0) {
                    user.setPoints(user.getPoints() + earnPoints);
                    // 增加成长值
                    user.setGrowthValue(user.getGrowthValue() + earnPoints);
                    
                    // 检查是否需要升级
                    checkAndUpgradeLevel(user);
                    
                    userRepository.save(user);
                    
                    PointTransaction pt = new PointTransaction();
                    pt.setUser(user);
                    pt.setAmount(earnPoints);
                    pt.setBalanceAfter(user.getPoints());
                    pt.setType("EARN");
                    pt.setRemark("订单消费赠送: " + order.getOrderNo());
                    pointTransactionRepository.save(pt);
                }
            } catch (Exception e) {
                // 积分逻辑异常不影响主支付流程
            }
        }
        orderRepository.save(order);
        saveTimeline(order, status);
    }

    private void checkAndUpgradeLevel(User user) {
        List<com.milktea.milktea_backend.model.entity.MemberLevel> levels = memberLevelRepository.findAll();
        levels.sort((l1, l2) -> l2.getMinGrowthValue().compareTo(l1.getMinGrowthValue())); // 按成长值降序
        
        for (com.milktea.milktea_backend.model.entity.MemberLevel level : levels) {
            if (user.getGrowthValue() >= level.getMinGrowthValue()) {
                user.setMemberLevel(level);
                break;
            }
        }
    }

    public OrderEstimatedTimeDTO getEstimatedTime(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        OrderEstimatedTimeDTO dto = new OrderEstimatedTimeDTO();
        
        // 计算排队位置：在当前订单之前，且状态为 PAID 或 MAKING 的订单数量
        long queuePosition = orderRepository.countByStoreIdAndStatusInAndOrderTimeBefore(
                order.getStore().getId(),
                Arrays.asList("PAID", "MAKING"),
                order.getOrderTime());
        
        // 预估等待时间：排队人数 * 5分钟 (假设每单5分钟)
        int estimatedWaitTime = (int) (queuePosition + 1) * 5;
        
        dto.setEstimatedWaitTime(estimatedWaitTime);
        dto.setQueuePosition((int) queuePosition + 1);
        dto.setCurrentStep(order.getStatus());
        dto.setNextStep(getNextStep(order.getStatus()));
        dto.setEstimatedReadyTime(order.getEstimatedReadyTime() != null ? order.getEstimatedReadyTime() : LocalDateTime.now().plusMinutes(15));
        dto.setProgressPercentage(calculateProgress(order.getStatus()));
        return dto;
    }

    private String getNextStep(String status) {
        switch (status) {
            case "PAID": return "MAKING";
            case "MAKING": return "READY";
            case "READY": return "DELIVERING";
            case "DELIVERING": return "DELIVERED";
            case "DELIVERED": return "COMPLETED";
            case "COMPLETED": return "REVIEWED";
            default: return "FINISHED";
        }
    }

    private int calculateProgress(String status) {
        switch (status) {
            case "PENDING_PAYMENT": return 10;
            case "PAID": return 20;
            case "MAKING": return 40;
            case "READY": return 60;
            case "DELIVERING": return 80;
            case "DELIVERED": return 90;
            case "COMPLETED":
            case "REVIEWED":
            case "FINISHED": return 100;
            case "REFUNDING":
            case "REFUNDED":
            case "CANCELLED": return 0;
            default: return 0;
        }
    }

    public Map<String, Long> getOrderStatusCounts(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
    }

    public OrderStatisticsDTO getOrderStatistics(Long userId) {
        OrderStatisticsDTO dto = new OrderStatisticsDTO();
        dto.setTotalOrders(orderRepository.countByUserId(userId));
        dto.setStatusCounts(getOrderStatusCounts(userId));
        dto.setTotalSpent(orderRepository.sumActualAmountByUserId(userId));
        return dto;
    }

    public OrderProgressVisualDTO getProgressVisual(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));

        List<OrderStatusTimeline> timelines = timelineRepository.findByOrderIdOrderByCreatedAtAsc(order.getId());
        
        OrderProgressVisualDTO dto = new OrderProgressVisualDTO();
        List<OrderProgressVisualDTO.ProgressStepDTO> steps = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        // 定义所有可能的步骤
        String[] allSteps = {"PAID", "MAKING", "READY", "DELIVERED"};
        String[] stepNames = {"已支付", "制作中", "待取餐", "已完成"};
        String[] icons = {"check-circle", "coffee", "shopping-cart", "smile"};
        
        for (int i = 0; i < allSteps.length; i++) {
            String stepCode = allSteps[i];
            Optional<OrderStatusTimeline> timeline = timelines.stream()
                    .filter(t -> t.getStatus().equals(stepCode))
                    .findFirst();
            
            boolean completed = timeline.isPresent();
            boolean current = order.getStatus().equals(stepCode);
            String time = timeline.map(t -> t.getCreatedAt().format(formatter)).orElse("");
            
            steps.add(createStep(stepCode, stepNames[i], completed, time, icons[i], 
                    completed ? "52c41a" : "bfbfbf", current, completed ? 100 : 0));
        }
        
        dto.setSteps(steps);
        dto.setTotalSteps(allSteps.length);
        dto.setCurrentStepIndex(getCurrentStepIndex(order.getStatus(), allSteps));
        dto.setProgressPercentage(calculateProgress(order.getStatus()));
        return dto;
    }

    private int getCurrentStepIndex(String status, String[] allSteps) {
        for (int i = 0; i < allSteps.length; i++) {
            if (allSteps[i].equals(status)) return i;
        }
        return 0;
    }

    private OrderProgressVisualDTO.ProgressStepDTO createStep(String step, String name, boolean completed, String time, String icon, String color, boolean current, int progress) {
        OrderProgressVisualDTO.ProgressStepDTO s = new OrderProgressVisualDTO.ProgressStepDTO();
        s.setStep(step);
        s.setName(name);
        s.setCompleted(completed);
        s.setTime(time);
        s.setIcon(icon);
        s.setColor(color);
        s.setCurrent(current);
        s.setProgress(progress);
        return s;
    }

    /**
     * 获取订单状态的中文名称
     * 零回归原则：仅用于展示，不改变原有逻辑
     */
    public String getStatusChineseName(String status) {
        if (status == null) return "未知状态";
        switch (status.toUpperCase()) {
            case "PENDING_PAYMENT": return "待支付";
            case "PAID": return "已支付/待接单";
            case "MAKING": return "制作中";
            case "READY": return "待取餐";
            case "DELIVERING": return "配送中";
            case "DELIVERED": return "已送达";
            case "COMPLETED": return "已完成";
            case "FINISHED": return "已结束";
            case "REFUNDING": return "退款中";
            case "REFUNDED": return "已退款";
            case "CANCELLED": return "已取消";
            case "REVIEWED": return "已评价";
            case "APPEALING": return "申诉中";
            default: return status;
        }
    }

    @Transactional
    public void applyRefund(String orderNo, String reason) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setStatus("REFUNDING");
        order.setRefundReason(reason); // 记录退款原因
        orderRepository.save(order);
        saveTimeline(order, "REFUNDING");
    }

    @Transactional
    public void approveRefund(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        if (!"REFUNDING".equals(order.getStatus())) {
            throw new ServiceException("INVALID_STATUS", "订单不在退款申请中");
        }
        
        // 退钱给用户钱包
        User user = order.getUser();
        BigDecimal refundAmount = order.getActualAmount();
        user.setBalance((user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO).add(refundAmount));
        userRepository.save(user);
        
        order.setStatus("REFUNDED");
        orderRepository.save(order);
        saveTimeline(order, "REFUNDED");
    }

    public String getRefundStatus(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        return order.getStatus();
    }

    @Transactional
    public OrderRemindResponseDTO remindOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        int count = (order.getRemindCount() == null ? 0 : order.getRemindCount()) + 1;
        order.setRemindCount(count);
        order.setLastRemindTime(LocalDateTime.now());
        orderRepository.save(order);

        OrderRemindResponseDTO dto = new OrderRemindResponseDTO();
        dto.setRemindCount(count);
        dto.setMaxRemindCount(3);
        dto.setNextRemindTime(LocalDateTime.now().plusMinutes(30));
        dto.setMessage("已通知商家尽快制作,今日还可催单" + (3 - count) + "次");
        dto.setSuccess(true);
        return dto;
    }

    @Transactional
    public void submitReview(String orderNo, Map<String, Object> reviewData) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        // 异常熔断：评价逻辑独立，不影响订单状态更新的安全性
        // 注意：在 @Transactional 方法中，如果内部抛出异常，即使被 catch，事务也会被标记为 rollback-only。
        // 为了实现真正的“异常熔断”，我们将保存逻辑委托给一个非事务性或独立事务的方法。
        try {
            applicationContext.getBean(OrderService.class).saveReviewInternal(order, reviewData);
        } catch (Exception e) {
            // 静默失败，不中断主流程
        }
        
        order.setIsCommented(true);
        order.setStatus("REVIEWED"); // 14. 变成已评价
        orderRepository.save(order);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void saveReviewInternal(Order order, Map<String, Object> reviewData) {
        // 重新从数据库加载实体，确保在当前新事务中是受管状态
        Order managedOrder = orderRepository.findById(order.getId()).orElseThrow();
        
        com.milktea.milktea_backend.model.entity.OrderReview review = new com.milktea.milktea_backend.model.entity.OrderReview();
        review.setOrder(managedOrder);
        review.setUser(managedOrder.getUser());
        review.setStore(managedOrder.getStore()); // 14. 关联店铺
        
        // 综合评分
        if (reviewData.containsKey("score")) {
            review.setScore(((Number) reviewData.get("score")).intValue());
        } else {
            review.setScore(5); // 默认5分
        }

        // 配送评分
        if (reviewData.containsKey("deliveryScore")) {
            review.setDeliveryScore(((Number) reviewData.get("deliveryScore")).intValue());
        } else {
            review.setDeliveryScore(5);
        }

        // 商品评分
        if (reviewData.containsKey("productScore")) {
            review.setProductScore(((Number) reviewData.get("productScore")).intValue());
        } else {
            review.setProductScore(5);
        }

        // 关联商品
        if (reviewData.containsKey("productId") && reviewData.get("productId") != null) {
            Long productId = Long.valueOf(reviewData.get("productId").toString());
            productRepository.findById(productId).ifPresent(review::setProduct);
        }

        review.setContent((String) reviewData.get("content"));
        // 处理图片列表
        if (reviewData.containsKey("images")) {
            try {
                review.setImagesJson(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(reviewData.get("images")));
            } catch (Exception e) {
                // 忽略序列化错误
            }
        }
        
        orderReviewRepository.save(review);
    }

    @Transactional
    public void submitComplaint(Long userId, Map<String, Object> complaintData) {
        com.milktea.milktea_backend.model.entity.Complaint complaint = new com.milktea.milktea_backend.model.entity.Complaint();
        complaint.setUser(userRepository.findById(userId).orElseThrow());
        if (complaintData.containsKey("orderNo")) {
            orderRepository.findByOrderNo((String) complaintData.get("orderNo")).ifPresent(complaint::setOrder);
        }
        complaint.setType((String) complaintData.get("type"));
        complaint.setContent((String) complaintData.get("content"));
        complaint.setPhone((String) complaintData.get("phone"));
        complaint.setStatus("PENDING");
        
        if (complaintData.containsKey("images")) {
            try {
                complaint.setImagesJson(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(complaintData.get("images")));
            } catch (Exception e) {
            }
        }
        
        complaintRepository.save(complaint);
    }

    public List<com.milktea.milktea_backend.model.entity.Complaint> getUserComplaints(Long userId) {
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 发起支付
     */
    public String initiatePayment(String orderNo, String method) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        if ("ALIPAY".equalsIgnoreCase(method)) {
            return "https://openapi.alipaydev.com/gateway.do?app_id=2021000117650001&method=alipay.trade.page.pay&out_trade_no="
                    + orderNo + "&total_amount=" + order.getActualAmount() + "&subject=奶茶订单支付";
        }
        
        // 从系统配置获取支付网关配置
        String gatewayUrl = systemConfigRepository.findById("payment_gateway_url")
                .map(com.milktea.milktea_backend.model.entity.SystemConfig::getConfigValue)
                .orElse("https://payment-gateway.example.com/");
        
        return gatewayUrl + method.toLowerCase() + "?orderNo=" + orderNo + "&amount=" + order.getActualAmount();
    }

    private String generateOrderNo() {
        return "T" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public Optional<Order> getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    public List<OrderDTO> getUserOrders(Long userId, String status) {
        List<Order> allUserOrders = orderRepository.findByUserId(userId);
        System.out.println("用户 " + userId + " 的总订单数: " + allUserOrders.size());
        
        Stream<Order> orderStream = allUserOrders.stream();
        
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            String upperStatus = status.trim().toUpperCase();
            List<String> targetStatuses;
            switch (upperStatus) {
                case "COMPLETED":
                    targetStatuses = Arrays.asList("COMPLETED", "FINISHED", "REVIEWED", "DELIVERED");
                    break;
                case "PAID":
                    targetStatuses = Arrays.asList("PAID");
                    break;
                case "MAKING":
                    targetStatuses = Arrays.asList("MAKING");
                    break;
                case "READY":
                    targetStatuses = Arrays.asList("READY");
                    break;
                case "DELIVERING":
                    targetStatuses = Arrays.asList("DELIVERING");
                    break;
                case "REFUNDING":
                    targetStatuses = Arrays.asList("REFUNDING");
                    break;
                case "PENDING_PAYMENT":
                    targetStatuses = Arrays.asList("PENDING_PAYMENT");
                    break;
                case "CANCELLED":
                    targetStatuses = Arrays.asList("CANCELLED");
                    break;
                default:
                    targetStatuses = Collections.singletonList(upperStatus);
            }
            orderStream = orderStream.filter(o -> targetStatuses.contains(o.getStatus()));
        }
        
        return orderStream
                .sorted((o1, o2) -> {
                    LocalDateTime t1 = o1.getOrderTime() != null ? o1.getOrderTime() : o1.getCreatedAt();
                    LocalDateTime t2 = o2.getOrderTime() != null ? o2.getOrderTime() : o2.getCreatedAt();
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return t2.compareTo(t1);
                })
                .map(this::convertToDTO)
                .peek(dto -> {
                    if (dto.getOrderTime() == null) dto.setOrderTime(dto.getCreatedAt());
                    if (dto.getPayAmount() == null) dto.setPayAmount(dto.getTotalAmount());
                    System.out.println("返回订单: " + dto.getOrderNo() + ", 状态: " + dto.getStatus());
                })
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUser().getId());
        dto.setNickname(order.getUser().getNickname());
        dto.setStoreId(order.getStore().getId());
        dto.setStoreName(order.getStore().getName());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getActualAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setPayMethod(order.getPayMethod());
        dto.setTransactionId(order.getTransactionId());
        dto.setPayTime(order.getPayTime());
        dto.setOrderTime(order.getOrderTime());
        dto.setDeliveryType(order.getDeliveryType());
        dto.setAddressJson(order.getAddressJson());
        dto.setPickupCode(order.getPickupCode());
        dto.setQueueNumber(order.getQueueNumber());
        dto.setEstimatedReadyTime(order.getEstimatedReadyTime());
        dto.setActualReadyTime(order.getActualReadyTime());
        dto.setRemark(order.getRemark());
        dto.setRemindCount(order.getRemindCount());
        dto.setLastRemindTime(order.getLastRemindTime());
        dto.setIsCommented(order.getIsCommented());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream().map(item -> {
                OrderDTO.OrderItemDTO itemDto = new OrderDTO.OrderItemDTO();
                itemDto.setId(item.getId());
                if (item.getProduct() != null) {
                    itemDto.setProductId(item.getProduct().getId());
                }
                itemDto.setProductName(item.getProductName());
                itemDto.setProductImage(item.getProductImage());
                itemDto.setSpecJson(item.getSpecJson());
                itemDto.setPrice(item.getPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setTotalPrice(item.getTotalPrice());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    @Transactional
    public void cancelOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        // 只有待支付状态可以取消
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new ServiceException("ORDER_CANNOT_CANCEL", "订单当前状态不可取消");
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        saveTimeline(order, "CANCELLED");
    }

    @Transactional
    public void confirmOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        // 只有已送达状态可以确认收货
        if (!"DELIVERED".equals(order.getStatus()) && !"READY".equals(order.getStatus()) && !"DELIVERING".equals(order.getStatus())) {
            throw new ServiceException("ORDER_CANNOT_CONFIRM", "订单当前状态不可确认收货");
        }
        order.setStatus("COMPLETED");
        orderRepository.save(order);
        saveTimeline(order, "COMPLETED");
    }

    @Transactional
    public void submitAppeal(String orderNo, Map<String, Object> appealData) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        com.milktea.milktea_backend.model.entity.OrderAppeal appeal = new com.milktea.milktea_backend.model.entity.OrderAppeal();
        appeal.setOrder(order);
        appeal.setUser(order.getUser());
        appeal.setReason((String) appealData.get("reason"));
        appeal.setDescription((String) appealData.get("description"));
        appeal.setAmount(new java.math.BigDecimal(appealData.get("amount").toString()));
        appeal.setStatus("PENDING");
        
        orderAppealRepository.save(appeal);
        
        order.setStatus("APPEALING");
        orderRepository.save(order);
    }

    private void simulateOrderQueue(Order order) {
        // 14. 模拟排队逻辑：零回归原则，使用独立线程异步处理，不阻塞主流程
        new Thread(() -> {
            try {
                // 1. 模拟排队 10-30s
                int queueTime = new Random().nextInt(21) + 10;
                Thread.sleep(queueTime * 1000);
                updateStatusSilently(order.getOrderNo(), "MAKING");
                
                // 2. 模拟制作 5-20s
                int makingTime = new Random().nextInt(16) + 5;
                Thread.sleep(makingTime * 1000);
                
                if ("DELIVERY".equals(order.getDeliveryType())) {
                    updateStatusSilently(order.getOrderNo(), "DELIVERING");
                    
                    // 3. 配送 30min 自动送达 (演示环境缩短为 60s，生产环境应为 30*60)
                    // Thread.sleep(30 * 60 * 1000);
                    Thread.sleep(60 * 1000);
                    updateStatusSilently(order.getOrderNo(), "DELIVERED");
                } else {
                    updateStatusSilently(order.getOrderNo(), "READY");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // 异常熔断：模拟逻辑报错不影响主业务
            }
        }).start();
    }

    private void updateStatusSilently(String orderNo, String status) {
        try {
            // 重新获取订单以避免并发修改问题
            Order order = orderRepository.findByOrderNo(orderNo).orElse(null);
            if (order != null && !"CANCELLED".equals(order.getStatus()) && !"REFUNDED".equals(order.getStatus())) {
                order.setStatus(status);
                orderRepository.save(order);
                saveTimeline(order, status);
            }
        } catch (Exception e) {
            // 静默处理
        }
    }

}
