package com.milktea.backend.controller;

import com.milktea.backend.dto.AboutUsDTO;
import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.AboutUsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/app/about")
@RequiredArgsConstructor
public class AppAboutUsController {

    private final AboutUsService aboutUsService;

    @GetMapping
    public ApiResponse<AboutUsDTO> getAboutUs() {
        return ApiResponse.success(aboutUsService.getAboutUs());
    }
}