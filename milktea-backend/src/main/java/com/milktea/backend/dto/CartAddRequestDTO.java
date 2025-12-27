package com.milktea.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartAddRequestDTO {
    private Long storeId;
    private Long productId;
    private Long specId;
    private String sweetness;
    private String temperature;
    private List<Long> toppings;
    private Integer quantity;
}