package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.PromotionService;
import com.milktea.milktea_backend.model.entity.Promotion;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app/promotions")
public class AppPromotionController {

    private final PromotionService promotionService;

    public AppPromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public ApiResponse<List<Promotion>> getAllPromotions() {
        return ApiResponse.success(promotionService.getAllActivePromotions());
    }

    @GetMapping("/full-reduction")
    public ApiResponse<List<Promotion>> getFullReductionPromotions() {
        return ApiResponse.success(promotionService.getPromotionsByType("FULL_REDUCE"));
    }

    @GetMapping("/second-half/{id}")
    public ApiResponse<Promotion> getSecondHalfDetail(@PathVariable Long id) {
        return ApiResponse.success(promotionService.getPromotionDetail(id));
    }

    @GetMapping("/flash-sale")
    public ApiResponse<List<Promotion>> getFlashSalePromotions() {
        return ApiResponse.success(promotionService.getPromotionsByType("FLASH_SALE"));
    }
}