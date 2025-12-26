package com.milktea.backend.dto;

import com.milktea.milktea_backend.model.entity.UserAddress;
import com.milktea.milktea_backend.model.entity.UserCoupon;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutPrepareDTO {
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private UserAddress defaultAddress;
    private List<UserCoupon> availableCoupons;
    private Integer estimatedWaitTime;

    @Data
    public static class CartItemDTO {
        private Long id;
        private String productName;
        private String productImage;
        private String specDescription;
        private BigDecimal price;
        private Integer quantity;
    }
}