package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PriceCalculationResponseDTO {
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private List<PriceBreakdownItem> breakdown;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceBreakdownItem {
        private String name;
        private BigDecimal amount;
    }
}