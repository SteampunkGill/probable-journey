package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.SearchService;
import com.milktea.milktea_backend.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app/search")
@RequiredArgsConstructor
public class AppSearchController {

    private final SearchService searchService;

    @GetMapping
    public ApiResponse<List<Product>> searchProducts(@RequestAttribute(value = "userId", required = false) Long userId, @RequestParam String keyword) {
        return ApiResponse.success(searchService.searchProducts(userId, keyword));
    }

    @GetMapping("/history")
    public ApiResponse<List<String>> getSearchHistory(@RequestAttribute("userId") Long userId) {
        return ApiResponse.success(searchService.getSearchHistory(userId));
    }

    @DeleteMapping("/history")
    public ApiResponse<Void> clearHistory(@RequestAttribute("userId") Long userId) {
        searchService.clearHistory(userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/hot")
    public ApiResponse<List<String>> getHotKeywords() {
        return ApiResponse.success(searchService.getHotKeywords());
    }
}