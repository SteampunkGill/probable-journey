package com.milktea.backend.dto;

import lombok.Data;

@Data
public class CartUpdateDTO {
    private Long cartItemId;
    private Integer quantity;
}