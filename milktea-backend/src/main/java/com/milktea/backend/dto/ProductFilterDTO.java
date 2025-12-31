package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductFilterDTO {
    private Long categoryId;
    private String keyword;
    private String sweetness;
    private String temperature;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sort;
    private Integer page = 1;
    private Integer size = 20;
}