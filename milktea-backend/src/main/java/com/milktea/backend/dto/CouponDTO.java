package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponDTO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private String validityType;
    private Integer validityDays;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // 可领取状态或用户持有状态
}