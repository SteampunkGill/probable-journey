package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.BannerService;
import com.milktea.milktea_backend.model.entity.Banner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app/banners")
@RequiredArgsConstructor
public class AppBannerController {

    private final BannerService bannerService;

    @GetMapping
    public ApiResponse<List<Banner>> getBanners() {
        return ApiResponse.success(bannerService.findAll());
    }
}