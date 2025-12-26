package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayMetricsDTO {
    private Integer orderCount;
    private BigDecimal salesAmount;
    private BigDecimal avgOrderValue;
    private BigDecimal customerUnitPrice;
    private Integer newUsers;
    private Double conversionRate;
    private Map<String, String> comparedToYesterday;
}