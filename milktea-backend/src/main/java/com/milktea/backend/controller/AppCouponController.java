package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.CouponDTO;
import com.milktea.backend.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/app/coupons")
public class AppCouponController {

    private final CouponService couponService;

    public AppCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/available")
    public ApiResponse<List<CouponDTO>> getAvailableCoupons() {
        return ApiResponse.success(couponService.getAvailableCoupons());
    }

    @PostMapping("/receive")
    public ApiResponse<Void> receiveCoupon(@RequestBody Map<String, Long> body) {
        couponService.receiveCoupon(body.get("id"));
        return ApiResponse.success(null);
    }

    @GetMapping("/my")
    public ApiResponse<List<CouponDTO>> getMyCoupons() {
        return ApiResponse.success(couponService.getMyCoupons());
    }

    @GetMapping("/{id}")
    public ApiResponse<CouponDTO> getCouponDetail(@PathVariable Long id) {
        return ApiResponse.success(couponService.getCouponDetail(id));
    }
}