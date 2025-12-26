package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.PushTaskDTO;
import com.milktea.backend.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/push")
public class AdminPushController {

    private final NotificationService notificationService;

    public AdminPushController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/tasks")
    public ApiResponse<PushTaskDTO> createPushTask(@RequestBody PushTaskDTO taskDTO) {
        return ApiResponse.success(notificationService.createPushTask(taskDTO));
    }

    @GetMapping("/tasks")
    public ApiResponse<Page<PushTaskDTO>> getPushTasks(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(notificationService.getPushTasks(PageRequest.of(page - 1, size)));
    }

    @GetMapping("/tasks/{id}/statistics")
    public ApiResponse<Map<String, Object>> getPushStatistics(@PathVariable Long id) {
        return ApiResponse.success(notificationService.getPushStatistics(id));
    }

    @PostMapping("/activity-notice")
    public ApiResponse<Void> sendActivityNotice(@RequestBody Map<String, Object> notice) {
        notificationService.sendActivityNotice(notice);
        return ApiResponse.success(null);
    }
}