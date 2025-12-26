package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import com.milktea.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/today-metrics")
    public ApiResponse<TodayMetricsDTO> getTodayMetrics() {
        return ApiResponse.success(dashboardService.getTodayMetrics());
    }

    @GetMapping("/realtime-sales")
    public ApiResponse<RealtimeSalesDTO> getRealtimeSales() {
        return ApiResponse.success(dashboardService.getRealtimeSales());
    }

    @GetMapping("/sales-trend")
    public ApiResponse<SalesTrendDTO> getSalesTrend() {
        return ApiResponse.success(dashboardService.getSalesTrend());
    }

    @GetMapping("/product-ranking")
    public ApiResponse<List<ProductRankingDTO>> getProductRanking() {
        return ApiResponse.success(dashboardService.getProductRanking());
    }

    @GetMapping("/inventory-alerts")
    public ApiResponse<List<InventoryAlertDTO>> getInventoryAlerts() {
        return ApiResponse.success(dashboardService.getInventoryAlerts());
    }

    @GetMapping("/order-alerts")
    public ApiResponse<List<OrderAlertDTO>> getOrderAlerts() {
        return ApiResponse.success(dashboardService.getOrderAlerts());
    }

    @GetMapping("/notification")
    public ApiResponse<List<NotificationDTO>> getNotifications() {
        return ApiResponse.success(dashboardService.getNotifications());
    }
}