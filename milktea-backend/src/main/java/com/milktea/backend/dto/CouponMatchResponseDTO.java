package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CouponMatchResponseDTO {
    private CouponInfo bestMatch;
    private List<CouponInfo> alternativeMatches;
    private String suggestion;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CouponInfo {
        private Long id;
        private String name;
        private String type;
        private BigDecimal value;
        private BigDecimal saving;
        private String condition;
    }
}