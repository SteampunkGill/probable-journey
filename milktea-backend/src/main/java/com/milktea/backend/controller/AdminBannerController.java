package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.BannerService;
import com.milktea.milktea_backend.model.entity.Banner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping("/banners")
    public ApiResponse<List<Banner>> listBanners() {
        return ApiResponse.success(bannerService.findAll());
    }

    @PostMapping("/banners")
    public ApiResponse<Banner> saveBanner(@RequestBody Banner banner) {
        return ApiResponse.success(bannerService.saveOrUpdate(banner));
    }

    @PutMapping("/banners/{id}")
    public ApiResponse<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        return ApiResponse.success(bannerService.saveOrUpdate(banner));
    }

    @GetMapping("/ad-positions")
    public ApiResponse<List<Map<String, Object>>> listAdPositions() {
        return ApiResponse.success(bannerService.findAllPositions());
    }

    @PutMapping("/ad-positions/{id}")
    public ApiResponse<Map<String, Object>> updateAdPosition(@PathVariable Long id, @RequestBody Map<String, Object> positionData) {
        return ApiResponse.success(bannerService.updatePosition(id, positionData));
    }

    @DeleteMapping("/banners/{id}")
    public ApiResponse<Void> deleteBanner(@PathVariable Long id) {
        bannerService.delete(id);
        return ApiResponse.success(null);
    }
}