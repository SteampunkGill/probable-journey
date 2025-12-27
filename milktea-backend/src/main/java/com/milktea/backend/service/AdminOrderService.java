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

    public AdminOrderService(OrderRepository orderRepository,
                             OrderRefundRepository orderRefundRepository,
                             ComplaintRepository complaintRepository,
                             com.milktea.backend.repository.PrintTemplateRepository printTemplateRepository,
                             com.milktea.backend.repository.PrintPrinterRepository printPrinterRepository) {
        this.orderRepository = orderRepository;
        this.orderRefundRepository = orderRefundRepository;
        this.complaintRepository = complaintRepository;
        this.printTemplateRepository = printTemplateRepository;
        this.printPrinterRepository = printPrinterRepository;
    }

    public List<OrderDTO> getPendingOrders() {
        return orderRepository.findByStatus("PAID").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
        order.setStatus("READY");
        order.setActualReadyTime(LocalDateTime.now());
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
        if (orderNo != null) {
            orders = orderRepository.findByOrderNo(orderNo).map(List::of).orElse(List.of());
        } else if (storeId != null && status != null) {
            orders = orderRepository.findByStoreIdAndStatus(storeId, status);
        } else if (storeId != null) {
            orders = orderRepository.findByStoreId(storeId);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setNickname(order.getUser().getNickname());
        }
        if (order.getStore() != null) {
            dto.setStoreId(order.getStore().getId());
            dto.setStoreName(order.getStore().getName());
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
               .append(order.getUser().getId()).append(",")
               .append(order.getStore().getId()).append(",")
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
        }
        orderRefundRepository.save(refund);
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
}