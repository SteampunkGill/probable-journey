package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import com.milktea.backend.service.OrderService;
import com.milktea.milktea_backend.model.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/app/orders")
@RequiredArgsConstructor
public class AppOrderController {

    private final OrderService orderService;
    private final com.milktea.backend.service.UserService userService;

    @PostMapping("/create")
    public ApiResponse<Order> createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        Long userId = userService.getCurrentUser().getId();
        return ApiResponse.success(orderService.createOrderFromDTO(userId, orderCreateDTO));
    }

    @PutMapping("/{orderNo}/remark")
    public ApiResponse<Order> updateRemark(@PathVariable String orderNo, @RequestBody Map<String, String> body) {
        String remark = body.get("remark");
        return ApiResponse.success(orderService.updateOrderRemark(orderNo, remark));
    }

    @GetMapping("/{orderNo}/status")
    public ApiResponse<String> getOrderStatus(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.getOrderByOrderNo(orderNo)
                .map(Order::getStatus)
                .orElse("NOT_FOUND"));
    }

    @GetMapping("/{orderNo}/estimated-time")
    public ApiResponse<OrderEstimatedTimeDTO> getEstimatedTime(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.getEstimatedTime(orderNo));
    }

    @GetMapping
    public ApiResponse<List<OrderDTO>> getOrders(@RequestParam(required = false) String status) {
        Long userId = userService.getCurrentUser().getId();
        return ApiResponse.success(orderService.getUserOrders(userId, status));
    }

    @GetMapping("/status-count")
    public ApiResponse<Map<String, Long>> getStatusCount() {
        Long userId = userService.getCurrentUser().getId();
        return ApiResponse.success(orderService.getOrderStatusCounts(userId));
    }

    @GetMapping("/statistics")
    public ApiResponse<OrderStatisticsDTO> getStatistics() {
        Long userId = userService.getCurrentUser().getId();
        return ApiResponse.success(orderService.getOrderStatistics(userId));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<Order> getOrderDetail(@PathVariable String orderNo) {
        return orderService.getOrderByOrderNo(orderNo)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "订单不存在"));
    }

    @GetMapping("/{orderNo}/progress-visual")
    public ApiResponse<OrderProgressVisualDTO> getProgressVisual(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.getProgressVisual(orderNo));
    }

    @PostMapping("/{orderNo}/refund")
    public ApiResponse<Void> applyRefund(@PathVariable String orderNo, @RequestBody Map<String, String> body) {
        orderService.applyRefund(orderNo, body.get("reason"));
        return ApiResponse.success(null);
    }

    @GetMapping("/{orderNo}/refund/status")
    public ApiResponse<String> getRefundStatus(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.getRefundStatus(orderNo));
    }

    @PostMapping("/{orderNo}/remind")
    public ApiResponse<OrderRemindResponseDTO> remindOrder(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.remindOrder(orderNo));
    }

    @PostMapping("/{orderNo}/review")
    public ApiResponse<Void> submitReview(@PathVariable String orderNo, @RequestBody Map<String, Object> reviewData) {
        orderService.submitReview(orderNo, reviewData);
        return ApiResponse.success(null);
    }

    @PostMapping("/complaint")
    public ApiResponse<Void> submitComplaint(@RequestBody Map<String, Object> complaintData) {
        Long userId = userService.getCurrentUser().getId();
        orderService.submitComplaint(userId, complaintData);
        return ApiResponse.success(null);
    }

    @PostMapping("/{orderNo}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return ApiResponse.success(null);
    }

    @PostMapping("/{orderNo}/confirm")
    public ApiResponse<Void> confirmOrder(@PathVariable String orderNo) {
        orderService.confirmOrder(orderNo);
        return ApiResponse.success(null);
    }

    @PostMapping("/{orderNo}/appeal")
    public ApiResponse<Void> submitAppeal(@PathVariable String orderNo, @RequestBody Map<String, Object> appealData) {
        orderService.submitAppeal(orderNo, appealData);
        return ApiResponse.success(null);
    }

}