package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/app/payment")
@RequiredArgsConstructor
public class AppPaymentController {

    private final OrderService orderService;

    @PostMapping("/alipay")
    public ApiResponse<String> initiateAlipay(@RequestBody Map<String, String> body) {
        String orderNo = body.get("orderNo");
        return ApiResponse.success(orderService.initiatePayment(orderNo, "ALIPAY"));
    }

    @GetMapping("/status/{orderNo}")
    public ApiResponse<String> getPaymentStatus(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.getOrderByOrderNo(orderNo)
                .map(order -> "PAID".equals(order.getStatus()) ? "SUCCESS" : "PENDING")
                .orElse("NOT_FOUND"));
    }

    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        String orderNo = params.get("out_trade_no");
        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            orderService.updateOrderStatusByNo(orderNo, "PAID");
        }
        return "success";
    }
}