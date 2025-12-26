package com.milktea.backend.controller;

import com.milktea.backend.dto.*;
import com.milktea.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app/cart")
@RequiredArgsConstructor
public class AppCartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<List<CheckoutPrepareDTO.CartItemDTO>> getCart(@RequestAttribute("userId") Long userId) {
        return ApiResponse.success(cartService.getCart(userId));
    }

    @PostMapping("/add")
    public ApiResponse<Void> addToCart(@RequestAttribute("userId") Long userId, @RequestBody CartAddRequestDTO request) {
        cartService.addToCart(userId, request);
        return ApiResponse.success(null);
    }

    @PutMapping("/update-quantity")
    public ApiResponse<Void> updateQuantity(@RequestAttribute("userId") Long userId, @RequestBody CartUpdateDTO updateDTO) {
        cartService.updateQuantity(userId, updateDTO);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/remove/{id}")
    public ApiResponse<Void> removeFromCart(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        cartService.removeFromCart(userId, id);
        return ApiResponse.success(null);
    }

    @PostMapping("/auto-match-coupons")
    public ApiResponse<CouponMatchResponseDTO> autoMatchCoupons(@RequestAttribute("userId") Long userId, @RequestBody CouponMatchRequestDTO request) {
        return ApiResponse.success(cartService.autoMatchCoupons(userId, request));
    }

    @PostMapping("/checkout")
    public ApiResponse<CheckoutPrepareDTO> prepareCheckout(@RequestAttribute("userId") Long userId) {
        return ApiResponse.success(cartService.prepareCheckout(userId));
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart(@RequestAttribute("userId") Long userId) {
        cartService.clearCart(userId);
        return ApiResponse.success(null);
    }

}