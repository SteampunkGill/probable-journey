package com.milktea.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointExchangeRecordDTO {
    private Long id;
    private String productName;
    private Integer pointCost;
    private LocalDateTime exchangeTime;
    private String status;
}