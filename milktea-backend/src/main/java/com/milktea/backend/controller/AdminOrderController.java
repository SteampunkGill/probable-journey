package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import com.milktea.backend.service.AdminOrderService;
import com.milktea.milktea_backend.model.entity.Complaint;
import com.milktea.milktea_backend.model.entity.Order;
import com.milktea.milktea_backend.model.entity.OrderRefund;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    // 7.1 订单处理
    @GetMapping("/orders/pending")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getPendingOrders() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getPendingOrders()));
    }

    @PostMapping("/orders/{orderNo}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptOrder(@PathVariable String orderNo) {
        adminOrderService.acceptOrder(orderNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/orders/{orderNo}/start-making")
    public ResponseEntity<ApiResponse<Void>> startMaking(@PathVariable String orderNo) {
        adminOrderService.startMaking(orderNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/orders/{orderNo}/ready")
    public ResponseEntity<ApiResponse<Void>> readyOrder(@PathVariable String orderNo) {
        adminOrderService.readyOrder(orderNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/orders/{orderNo}/complete")
    public ResponseEntity<ApiResponse<Void>> completeOrder(@PathVariable String orderNo) {
        adminOrderService.completeOrder(orderNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/orders/{orderNo}/deliver")
    public ResponseEntity<ApiResponse<Void>> deliverOrder(@PathVariable String orderNo) {
        adminOrderService.deliverOrder(orderNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/orders/batch-accept")
    public ResponseEntity<ApiResponse<Void>> batchAccept(@RequestBody List<String> orderNos) {
        adminOrderService.batchAccept(orderNos);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/orders/{orderNo}/print")
    public ResponseEntity<ApiResponse<String>> printOrder(@PathVariable String orderNo) {
        return ResponseEntity.ok(ApiResponse.success("小票已发送至打印机"));
    }

    @PostMapping("/notifications/new-order-alert")
    public ResponseEntity<ApiResponse<Void>> triggerNewOrderAlert() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/notifications/voice-subscribe")
    public ResponseEntity<ApiResponse<Void>> voiceSubscribe() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/notifications/voice-test")
    public ResponseEntity<ApiResponse<String>> voiceTest() {
        return ResponseEntity.ok(ApiResponse.success("语音测试成功"));
    }

    // 7.2 订单查询
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> searchOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.searchOrders(orderNo, status, storeId)));
    }

    @PostMapping("/orders/export")
    public ResponseEntity<byte[]> exportOrders(@RequestBody OrderExportRequestDTO dto) {
        byte[] data = adminOrderService.exportOrders(dto);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/print/batch-orders")
    public ResponseEntity<ApiResponse<String>> batchPrint(@RequestBody List<String> orderNos) {
        return ResponseEntity.ok(ApiResponse.success("批量打印任务已提交"));
    }

    @GetMapping("/orders/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderStatistics() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getOrderStatistics()));
    }

    // 7.3 售后处理
    @GetMapping("/refunds")
    public ResponseEntity<ApiResponse<List<OrderRefund>>> getRefundRequests() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getRefundRequests()));
    }

    @PostMapping("/refunds/{id}/review")
    public ResponseEntity<ApiResponse<Void>> reviewRefund(@PathVariable Long id, @RequestBody RefundReviewDTO dto) {
        adminOrderService.reviewRefund(id, dto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/orders/{orderNo}/refund-approve")
    public ResponseEntity<ApiResponse<Void>> approveRefund(@PathVariable String orderNo) {
        adminOrderService.approveRefundByOrderNo(orderNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<List<Complaint>>> getComplaints() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getComplaints()));
    }

    @PostMapping("/complaints/{id}/handle")
    public ResponseEntity<ApiResponse<Void>> handleComplaint(@PathVariable Long id, @RequestBody ComplaintHandleDTO dto) {
        adminOrderService.handleComplaint(id, dto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/appeals")
    public ResponseEntity<ApiResponse<List<com.milktea.milktea_backend.model.entity.OrderAppeal>>> getAppeals() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getAppeals()));
    }

    @PostMapping("/appeals/{id}/refund")
    public ResponseEntity<ApiResponse<Void>> refundAppeal(@PathVariable Long id) {
        adminOrderService.refundAppeal(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 7.4 打印管理
    @GetMapping("/print/templates")
    public ResponseEntity<ApiResponse<List<com.milktea.milktea_backend.model.entity.PrintTemplate>>> getPrintTemplates() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getPrintTemplates()));
    }

    @PutMapping("/print/templates/{id}")
    public ResponseEntity<ApiResponse<Void>> updatePrintTemplate(@PathVariable Long id, @RequestBody Map<String, Object> template) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/print/devices")
    public ResponseEntity<ApiResponse<List<com.milktea.milktea_backend.model.entity.PrintPrinter>>> getPrintDevices() {
        return ResponseEntity.ok(ApiResponse.success(adminOrderService.getPrintDevices()));
    }

    @PostMapping("/print/test")
    public ResponseEntity<ApiResponse<String>> testPrint() {
        return ResponseEntity.ok(ApiResponse.success("测试打印成功"));
    }
}