package com.milktea.backend.service;

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
    public void updateOrderStatusByNo(String orderNo, String status) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        String oldStatus = order.getStatus();
        order.setStatus(status);
        if ("PAID".equals(status) && !"PAID".equals(oldStatus)) {
            order.setPayTime(LocalDateTime.now());
            order.setQueueNumber(new Random().nextInt(100) + 1);
            order.setEstimatedReadyTime(LocalDateTime.now().plusMinutes(15));
            
            // 支付成功，增加积分 (1元 = 1积分)
            User user = order.getUser();
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
            case "READY": return "DELIVERED";
            default: return "COMPLETED";
        }
    }

    private int calculateProgress(String status) {
        switch (status) {
            case "PAID": return 25;
            case "MAKING": return 50;
            case "READY": return 75;
            case "DELIVERED": return 100;
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

    @Transactional
    public void applyRefund(String orderNo, String reason) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setStatus("REFUNDING");
        orderRepository.save(order);
        saveTimeline(order, "REFUNDING");
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
        
        com.milktea.milktea_backend.model.entity.OrderReview review = new com.milktea.milktea_backend.model.entity.OrderReview();
        review.setOrder(order);
        review.setUser(order.getUser());
        review.setScore(((Number) reviewData.get("score")).intValue());
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
        
        order.setIsCommented(true);
        orderRepository.save(order);
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

    /**
     * 发起支付
     */
    public String initiatePayment(String orderNo, String method) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        if ("ALIPAY".equalsIgnoreCase(method)) {
            // 模拟支付宝沙箱支付链接
            // 在实际项目中，这里会调用支付宝SDK生成支付表单
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

    public List<Order> findOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findOrdersByUserIdAndStatus(Long userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
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
        if (!"DELIVERED".equals(order.getStatus())) {
            throw new ServiceException("ORDER_CANNOT_CONFIRM", "订单当前状态不可确认收货");
        }
        order.setStatus("COMPLETED");
        orderRepository.save(order);
        saveTimeline(order, "COMPLETED");
    }

}
