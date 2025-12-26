package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.PriceCalculationRequestDTO;
import com.milktea.backend.dto.PriceCalculationResponseDTO;
import com.milktea.backend.dto.ProductFilterDTO;
import com.milktea.backend.service.AppProductService;
import com.milktea.backend.service.CartService;
import com.milktea.milktea_backend.model.entity.Category;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.ProductOption;
import com.milktea.milktea_backend.model.entity.ProductSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppProductController {

    private final AppProductService productService;
    private final CartService cartService;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories() {
        return ApiResponse.success(productService.getCategories());
    }

    @GetMapping("/products")
    public ApiResponse<List<Product>> getProducts(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(productService.getProducts(categoryId));
    }

    @GetMapping("/products/advanced-filter")
    public ApiResponse<Map<String, Object>> advancedFilter(ProductFilterDTO filter) {
        List<Product> list = productService.advancedFilter(filter);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return ApiResponse.success(data);
    }

    @GetMapping("/products/{id}")
    public ApiResponse<Product> getProductDetail(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductDetail(id));
    }

    @GetMapping("/products/{id}/customizations")
    public ApiResponse<Map<String, Object>> getCustomizations(@PathVariable Long id) {
        List<ProductSpec> specs = productService.getProductSpecs(id);
        List<ProductOption> options = productService.getProductOptions(id);
        Map<String, Object> data = new HashMap<>();
        data.put("specs", specs);
        data.put("options", options);
        return ApiResponse.success(data);
    }

    @PostMapping("/products/{id}/calculate-price")
    public ApiResponse<PriceCalculationResponseDTO> calculatePrice(@PathVariable Long id, @RequestBody PriceCalculationRequestDTO request) {
        return ApiResponse.success(cartService.calculatePrice(id, request));
    }

    @GetMapping("/products/{id}/reviews")
    public ApiResponse<List<com.milktea.milktea_backend.model.entity.OrderReview>> getProductReviews(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductReviews(id));
    }
}