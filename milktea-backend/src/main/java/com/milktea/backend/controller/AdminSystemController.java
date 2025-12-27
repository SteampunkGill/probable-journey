package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import com.milktea.backend.service.SystemService;
import com.milktea.milktea_backend.model.entity.SysOperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminSystemController {

    private final SystemService systemService;


    @GetMapping("/staff")
    public ApiResponse<List<StaffDTO>> getStaffList() {
        return ApiResponse.success(systemService.getAllStaff());
    }

    @PostMapping("/staff")
    public ApiResponse<StaffDTO> createStaff(@RequestBody StaffDTO staffDTO) {
        return ApiResponse.success(systemService.createStaff(staffDTO));
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleDTO>> getRoleList() {
        return ApiResponse.success(systemService.getAllRoles());
    }

    @PostMapping("/roles")
    public ApiResponse<RoleDTO> saveRole(@RequestBody RoleDTO roleDTO) {
        return ApiResponse.success(systemService.saveRole(roleDTO));
    }

    @GetMapping("/logs")
    public ApiResponse<List<SysOperationLog>> getLogs() {
        return ApiResponse.success(systemService.getLogs());
    }

    @PostMapping("/logs/record")
    public ApiResponse<Void> recordLog(@RequestBody Map<String, String> logData) {
        systemService.recordLog(logData.get("module"), logData.get("action"), logData.get("details"));
        return ApiResponse.success(null);
    }



    @PutMapping("/stores/{id}/business-hours")
    public ApiResponse<Void> setBusinessHours(@PathVariable Long id, @RequestBody List<BusinessHourDTO> hours) {

        return ApiResponse.success(null);
    }

    @PostMapping("/stores/switch")
    public ApiResponse<Map<String, Object>> switchStore(@RequestBody Map<String, Long> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("storeId", data.get("storeId"));
        result.put("token", "new-token-for-store-" + data.get("storeId"));
        return ApiResponse.success(result);
    }

    @PutMapping("/stores/{id}/business-status")
    public ApiResponse<Void> setBusinessStatus(@PathVariable Long id, @RequestBody Map<String, String> data) {
        systemService.updateBusinessStatus(id, data.get("status"));
        return ApiResponse.success(null);
    }

    @GetMapping("/stores/business-settings")
    public ApiResponse<Map<String, Object>> getGlobalBusinessSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("autoAcceptOrder", true);
        settings.put("globalBusinessStatus", "OPEN");
        return ApiResponse.success(settings);
    }

 

    @GetMapping("/settings")
    public ApiResponse<List<SystemConfigDTO>> getSettings() {
        return ApiResponse.success(systemService.getConfigs());
    }

    @PutMapping("/settings")
    public ApiResponse<Void> updateSettings(@RequestBody SystemConfigDTO configDTO) {
        systemService.updateConfig(configDTO);
        return ApiResponse.success(null);
    }

    @GetMapping("/backups")
    public ApiResponse<List<BackupDTO>> getBackups() {
        return ApiResponse.success(systemService.getBackups());
    }

    @PostMapping("/backups")
    public ApiResponse<BackupDTO> createBackup() {
        return ApiResponse.success(systemService.createBackup());
    }

    @PostMapping("/backups/{id}/restore")
    public ApiResponse<Void> restoreBackup(@PathVariable Long id) {
        systemService.restoreBackup(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/monitoring/performance")
    public ApiResponse<Map<String, Object>> getPerformanceMonitoring() {
        return ApiResponse.success(systemService.getPerformance());
    }

    @GetMapping("/monitoring/business")
    public ApiResponse<Map<String, Object>> getBusinessMonitoring() {
        return ApiResponse.success(systemService.getBusinessMonitoring());
    }
}