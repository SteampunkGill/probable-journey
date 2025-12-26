package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertDTO {
    private Long productId;
    private String productName;
    private String specName;
    private Integer currentStock;
    private Integer threshold;
    private String status; // "LOW_STOCK", "OUT_OF_STOCK"
}