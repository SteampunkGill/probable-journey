package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.service.CouponService;
import com.milktea.milktea_backend.model.entity.CouponTemplate;
import com.milktea.milktea_backend.model.entity.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping("/coupon-templates")
    public ApiResponse<List<CouponTemplate>> listTemplates() {
        return ApiResponse.success(couponService.findAllTemplates());
    }

    @PostMapping("/coupon-templates")
    public ApiResponse<CouponTemplate> createTemplate(@RequestBody CouponTemplate template) {
        return ApiResponse.success(couponService.createTemplate(template));
    }

    @PostMapping("/coupons/distribute")
    public ApiResponse<Void> distributeCoupons(@RequestBody Map<String, Object> params) {
        Long templateId = Long.valueOf(params.get("templateId").toString());
        List<Long> userIds = (List<Long>) params.get("userIds");
        couponService.distribute(templateId, userIds);
        return ApiResponse.success(null);
    }

    @GetMapping("/coupons/{id}/statistics")
    public ApiResponse<Map<String, Object>> couponStatistics(@PathVariable Long id) {
        return ApiResponse.success(couponService.getStatistics(id));
    }

    @GetMapping("/coupons/distribution-records")
    public ApiResponse<List<UserCoupon>> distributionRecords() {
        return ApiResponse.success(couponService.findAllDistributionRecords());
    }
}