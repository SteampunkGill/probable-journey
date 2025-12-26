package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MemberExclusivePriceDTO {
    private String level;
    private BigDecimal discountRate;
    private List<ProductPriceDTO> products;

    @Data
    public static class ProductPriceDTO {
        private Long productId;
        private String productName;
        private BigDecimal originalPrice;
        private BigDecimal memberPrice;
        private BigDecimal discount;
    }
}