package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.StoreService;
import com.milktea.milktea_backend.model.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/app/stores")
@RequiredArgsConstructor
public class AppStoreController {

    private final StoreService storeService;

    @GetMapping("/nearby")
    public ApiResponse<List<Store>> getNearbyStores(
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district) {
        
        if (latitude != null && longitude != null) {
            if (radius != null) {
                return ApiResponse.success(storeService.findNearbyStores(latitude, longitude, radius));
            } else {
                return ApiResponse.success(storeService.findAllOpenStoresOrderByDistance(latitude, longitude));
            }
        } else if (province != null || city != null || district != null) {
            return ApiResponse.success(storeService.findOpenStoresByRegion(province, city, district));
        } else {
            return ApiResponse.success(storeService.findOpenStores());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Store> getStoreDetail(@PathVariable Long id) {
        return storeService.getStoreById(id)
                .map(ApiResponse::success)
                .orElseThrow(() -> new com.milktea.backend.exception.ServiceException("STORE_NOT_FOUND", "门店不存在"));
    }
}