package com.milktea.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class OrderStatisticsDTO {
    private Long totalOrders;
    private Map<String, Long> statusCounts;
    private java.math.BigDecimal totalSpent;
}