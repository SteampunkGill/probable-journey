package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminProductDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Boolean isMemberExclusive;
    private BigDecimal memberPrice;
    private Integer stock;
    private Integer alertThreshold;
    private Integer sales;
    private Integer status;
    private Boolean isActive;
    private String sugarContent;
    private String calories;
    private String defaultSweetness;
    private String defaultTemperature;
    private String supportSweetness;
    private String supportTemperature;
}