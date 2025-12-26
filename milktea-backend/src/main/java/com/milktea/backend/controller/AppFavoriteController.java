package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.FavoriteService;
import com.milktea.milktea_backend.model.entity.UserFavoriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/app/favorites")
@RequiredArgsConstructor
@CrossOrigin
public class AppFavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ApiResponse<Page<UserFavoriteProduct>> getMyFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(favoriteService.getMyFavorites(page, size));
    }

    @PostMapping("/add")
    public ApiResponse<Void> addFavorite(@RequestBody Map<String, Long> body) {
        favoriteService.addFavorite(body.get("productId"));
        return ApiResponse.success(null);
    }

    @DeleteMapping("/remove/{productId}")
    public ApiResponse<Void> removeFavorite(@PathVariable Long productId) {
        favoriteService.removeFavorite(productId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearAll() {
        favoriteService.clearAll();
        return ApiResponse.success(null);
    }

    @GetMapping("/check/{productId}")
    public ApiResponse<Boolean> checkFavorite(@PathVariable Long productId) {
        return ApiResponse.success(favoriteService.isFavorite(productId));
    }
}