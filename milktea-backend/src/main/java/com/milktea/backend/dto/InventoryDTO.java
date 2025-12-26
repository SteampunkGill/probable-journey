package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryDTO {
    private Long id;
    private String name;
    private String unit;
    private BigDecimal stock;
    private BigDecimal alertThreshold;
    private Boolean isLowStock;
}