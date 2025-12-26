package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecipeDTO {
    private Long productId;
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
}