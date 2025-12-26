package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.ActivityService;
import com.milktea.milktea_backend.model.entity.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/activities")
@RequiredArgsConstructor
public class AdminActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ApiResponse<List<Promotion>> listActivities() {
        return ApiResponse.success(activityService.findAll());
    }

    @PostMapping
    public ApiResponse<Promotion> createActivity(@RequestBody Promotion promotion) {
        return ApiResponse.success(activityService.create(promotion));
    }

    @PutMapping("/{id}/time-control")
    public ApiResponse<Promotion> timeControl(@PathVariable Long id, @RequestBody Map<String, LocalDateTime> timeRange) {
        LocalDateTime startTime = timeRange.get("startTime");
        LocalDateTime endTime = timeRange.get("endTime");
        return ApiResponse.success(activityService.updateTime(id, startTime, endTime));
    }

    @GetMapping("/{id}/analysis")
    public ApiResponse<Map<String, Object>> activityAnalysis(@PathVariable Long id) {
        return ApiResponse.success(activityService.getAnalysis(id));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Promotion> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusMap) {
        Boolean isActive = statusMap.get("isActive");
        return ApiResponse.success(activityService.updateStatus(id, isActive));
    }
}