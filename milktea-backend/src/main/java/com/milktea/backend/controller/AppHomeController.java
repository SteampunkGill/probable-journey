package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.HomeDataDTO;
import com.milktea.backend.dto.RecommendationFeedbackDTO;
import com.milktea.backend.dto.NotificationDTO;
import com.milktea.backend.service.HomeService;
import com.milktea.backend.service.NotificationService;
import com.milktea.milktea_backend.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app/home")
@RequiredArgsConstructor
public class AppHomeController {

    private final HomeService homeService;
    private final NotificationService notificationService;

    @GetMapping("/page")
    public ApiResponse<HomeDataDTO> getHomePageData() {
        return ApiResponse.success(homeService.getHomePageData());
    }

    @GetMapping("/recommendations")
    public ApiResponse<List<Product>> getRecommendations(@RequestAttribute(value = "userId", required = false) Long userId) {
        return ApiResponse.success(homeService.getRecommendations(userId));
    }

    @PostMapping("/recommendation-feedback")
    public ApiResponse<Void> handleFeedback(@RequestAttribute("userId") Long userId, @RequestBody RecommendationFeedbackDTO feedbackDTO) {
        homeService.handleFeedback(userId, feedbackDTO);
        return ApiResponse.success(null);
    }

    @GetMapping("/notifications")
    public ApiResponse<List<NotificationDTO>> getNotifications(@RequestAttribute(value = "userId", required = false) Long userId) {
        return ApiResponse.success(notificationService.getUserNotifications(userId));
    }
}