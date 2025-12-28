package com.milktea.backend.service;

import com.milktea.backend.dto.*;
import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.ComplaintRepository;
import com.milktea.backend.repository.OrderRefundRepository;
import com.milktea.backend.repository.OrderRepository;
import com.milktea.milktea_backend.model.entity.Complaint;
import com.milktea.milktea_backend.model.entity.Order;
import com.milktea.milktea_backend.model.entity.OrderRefund;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderRefundRepository orderRefundRepository;
    private final ComplaintRepository complaintRepository;
    private final com.milktea.backend.repository.PrintTemplateRepository printTemplateRepository;
    private final com.milktea.backend.repository.PrintPrinterRepository printPrinterRepository;
    private final com.milktea.backend.repository.UserRepository userRepository;
    private final com.milktea.backend.repository.OrderAppealRepository orderAppealRepository;

    public AdminOrderService(OrderRepository orderRepository,
                             OrderRefundRepository orderRefundRepository,
                             ComplaintRepository complaintRepository,
                             com.milktea.backend.repository.PrintTemplateRepository printTemplateRepository,
                             com.milktea.backend.repository.PrintPrinterRepository printPrinterRepository,
                             com.milktea.backend.repository.UserRepository userRepository,
                             com.milktea.backend.repository.OrderAppealRepository orderAppealRepository) {
        this.orderRepository = orderRepository;
        this.orderRefundRepository = orderRefundRepository;
        this.complaintRepository = complaintRepository;
        this.printTemplateRepository = printTemplateRepository;
        this.printPrinterRepository = printPrinterRepository;
        this.userRepository = userRepository;
        this.orderAppealRepository = orderAppealRepository;
    }

    public List<OrderDTO> getPendingOrders() {
        try {
            return orderRepository.findByStatus("PAID").stream()
                    .map(o -> {
                        try {
                            return convertToDTO(o);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @Transactional
    public void acceptOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setStatus("MAKING");
        orderRepository.save(order);
    }

    @Transactional
    public void startMaking(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setStatus("MAKING");
        orderRepository.save(order);
    }

    @Transactional
    public void readyOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        if ("DELIVERY".equals(order.getDeliveryType())) {
            order.setStatus("DELIVERING");
        } else {
            order.setStatus("READY");
        }
        
        order.setActualReadyTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional
    public void deliverOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setStatus("DELIVERED");
        orderRepository.save(order);
    }

    @Transactional
    public void completeOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    @Transactional
    public void batchAccept(List<String> orderNos) {
        for (String orderNo : orderNos) {
            acceptOrder(orderNo);
        }
    }

    public List<OrderDTO> searchOrders(String orderNo, String status, Long storeId) {
        List<Order> orders;
        // 简化实现，实际应使用 Specification 或 QueryDSL
        // 零回归原则：处理空字符串，确保查询逻辑正确
        boolean hasOrderNo = orderNo != null && !orderNo.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasStoreId = storeId != null;

        try {
            if (hasOrderNo) {
                orders = orderRepository.findByOrderNo(orderNo).map(List::of).orElse(List.of());
            } else if (hasStoreId && hasStatus) {
                orders = orderRepository.findByStoreIdAndStatus(storeId, status);
            } else if (hasStoreId) {
                orders = orderRepository.findByStoreId(storeId);
            } else if (hasStatus) {
                orders = orderRepository.findByStatus(status);
            } else {
                // 默认按时间倒序排列，提升用户体验且更安全
                orders = orderRepository.findRecentOrders();
            }
            
            if (orders == null) return List.of();
            
            return orders.stream()
                    .map(o -> {
                        try {
                            return convertToDTO(o);
                        } catch (Exception e) {
                            // 异常熔断：单个订单转换失败不应影响整个列表显示
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 顶级异常捕获，确保接口不崩溃
            return List.of();
        }
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        
        // 零回归原则：防御性编程，处理可能的关联实体缺失或加载异常
        try {
            if (order.getUser() != null) {
                dto.setUserId(order.getUser().getId());
                dto.setNickname(order.getUser().getNickname());
            }
        } catch (Exception e) {
            dto.setNickname("未知用户");
        }

        try {
            if (order.getStore() != null) {
                dto.setStoreId(order.getStore().getId());
                dto.setStoreName(order.getStore().getName());
            }
        } catch (Exception e) {
            dto.setStoreName("未知门店");
        }

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

    public byte[] exportOrders(OrderExportRequestDTO dto) {
        // 真实导出逻辑：生成 CSV 格式数据
        List<Order> orders;
        if (dto.getStoreId() != null) {
            orders = orderRepository.findByStoreId(dto.getStoreId());
        } else {
            orders = orderRepository.findAll();
        }
        
        // 根据日期过滤 (简化实现)
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            LocalDateTime start = LocalDateTime.parse(dto.getStartDate() + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(dto.getEndDate() + "T23:59:59");
            orders = orders.stream()
                    .filter(o -> o.getOrderTime().isAfter(start) && o.getOrderTime().isBefore(end))
                    .collect(Collectors.toList());
        }

        StringBuilder csv = new StringBuilder("订单号,用户ID,门店ID,状态,总金额,实付金额,下单时间\n");
        for (Order order : orders) {
            csv.append(order.getOrderNo()).append(",")
               .append(order.getUser() != null ? order.getUser().getId() : "-").append(",")
               .append(order.getStore() != null ? order.getStore().getId() : "-").append(",")
               .append(order.getStatus()).append(",")
               .append(order.getTotalAmount()).append(",")
               .append(order.getActualAmount()).append(",")
               .append(order.getOrderTime()).append("\n");
        }
        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public Map<String, Object> getOrderStatistics() {
        long total = orderRepository.count();
        Map<String, Long> statusCounts = orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        return Map.of("total", total, "statusCounts", statusCounts);
    }

    public List<OrderRefund> getRefundRequests() {
        return orderRefundRepository.findByStatus("PENDING");
    }

    @Transactional
    public void reviewRefund(Long id, RefundReviewDTO dto) {
        OrderRefund refund = orderRefundRepository.findById(id)
                .orElseThrow(() -> new ServiceException("REFUND_NOT_FOUND", "退款申请不存在"));
        refund.setStatus(dto.getStatus());
        refund.setReply(dto.getReply());
        if ("APPROVED".equals(dto.getStatus())) {
            Order order = refund.getOrder();
            order.setStatus("REFUNDED");
            orderRepository.save(order);

            // 退款给用户钱包
            com.milktea.milktea_backend.model.entity.User user = order.getUser();
            if (user != null) {
                // 重新从数据库加载用户以确保状态最新
                user = userRepository.findById(user.getId()).orElse(user);
                java.math.BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : java.math.BigDecimal.ZERO;
                user.setBalance(currentBalance.add(order.getActualAmount()));
                userRepository.save(user);
            }
        }
        orderRefundRepository.save(refund);
    }

    @Transactional
    public void approveRefundByOrderNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ServiceException("ORDER_NOT_FOUND", "订单不存在"));
        
        // 退款给用户钱包
        com.milktea.milktea_backend.model.entity.User user = order.getUser();
        if (user != null) {
            user = userRepository.findById(user.getId()).orElse(user);
            java.math.BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : java.math.BigDecimal.ZERO;
            user.setBalance(currentBalance.add(order.getActualAmount()));
            userRepository.save(user);
        }
        
        order.setStatus("REFUNDED");
        orderRepository.save(order);
    }

    public List<Complaint> getComplaints() {
        return complaintRepository.findAll();
    }

    @Transactional
    public void handleComplaint(Long id, ComplaintHandleDTO dto) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ServiceException("COMPLAINT_NOT_FOUND", "投诉不存在"));
        complaint.setSolution(dto.getSolution());
        complaint.setRemark(dto.getRemark());
        complaint.setCompensation(dto.getCompensation());
        complaint.setCompensationType(dto.getCompensationType());
        complaint.setCompensationCouponId(dto.getCompensationCouponId());
        complaint.setStatus(dto.getStatus());
        complaintRepository.save(complaint);
    }

    public List<com.milktea.milktea_backend.model.entity.PrintTemplate> getPrintTemplates() {
        return printTemplateRepository.findAll();
    }

    public List<com.milktea.milktea_backend.model.entity.PrintPrinter> getPrintDevices() {
        return printPrinterRepository.findAll();
    }

    public List<com.milktea.milktea_backend.model.entity.OrderAppeal> getAppeals() {
        return orderAppealRepository.findAll();
    }

    @Transactional
    public void refundAppeal(Long id) {
        com.milktea.milktea_backend.model.entity.OrderAppeal appeal = orderAppealRepository.findById(id)
                .orElseThrow(() -> new ServiceException("APPEAL_NOT_FOUND", "申诉不存在"));
        
        if (!"PENDING".equals(appeal.getStatus())) {
            throw new ServiceException("INVALID_STATUS", "申诉已处理");
        }

        Order order = appeal.getOrder();
        com.milktea.milktea_backend.model.entity.User user = order.getUser();
        
        // 异常熔断：退款逻辑封装在 try-catch 中，确保申诉状态更新的安全性
        try {
            // 退款逻辑：退回用户钱包
            java.math.BigDecimal refundAmount = appeal.getAmount();
            if (refundAmount == null) {
                refundAmount = order.getActualAmount();
            }
            
            // 重新加载用户确保余额准确
            com.milktea.milktea_backend.model.entity.User latestUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new ServiceException("USER_NOT_FOUND", "用户不存在"));
            
            java.math.BigDecimal currentBalance = latestUser.getBalance() != null ? latestUser.getBalance() : java.math.BigDecimal.ZERO;
            latestUser.setBalance(currentBalance.add(refundAmount));
            userRepository.save(latestUser);

            appeal.setStatus("APPROVED");
            orderAppealRepository.save(appeal);

            order.setStatus("REFUNDED");
            orderRepository.save(order);
        } catch (Exception e) {
            throw new ServiceException("REFUND_FAILED", "退款执行失败: " + e.getMessage());
        }
    }
}