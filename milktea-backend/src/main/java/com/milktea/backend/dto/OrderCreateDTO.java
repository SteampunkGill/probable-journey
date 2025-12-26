package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateDTO {
    private Long storeId;
    private String deliveryType; // PICKUP, DELIVERY
    private String addressJson;
    private String remark;
    private Integer usePoints; // 使用的积分
    private Long couponId; // 使用的优惠券ID
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
        private List<OrderItemCustomizationDTO> customizations;
    }

    @Data
    public static class OrderItemCustomizationDTO {
        private String optionName;
        private String valueName;
        private BigDecimal additionalPrice;
    }
}