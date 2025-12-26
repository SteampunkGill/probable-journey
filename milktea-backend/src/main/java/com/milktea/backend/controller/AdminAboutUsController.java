package com.milktea.backend.controller;

import com.milktea.backend.dto.AboutUsDTO;
import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.AboutUsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/about-us")
@RequiredArgsConstructor
public class AdminAboutUsController {

    private final AboutUsService aboutUsService;

    @GetMapping
    public ApiResponse<AboutUsDTO> getAboutUs() {
        return ApiResponse.success(aboutUsService.getAboutUs());
    }

    @PutMapping
    public ApiResponse<AboutUsDTO> updateAboutUs(@RequestBody AboutUsDTO dto) {
        return ApiResponse.success(aboutUsService.updateAboutUs(dto));
    }
}