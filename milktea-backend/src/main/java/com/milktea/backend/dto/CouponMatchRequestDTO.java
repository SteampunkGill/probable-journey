package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CouponMatchRequestDTO {
    private List<Long> cartItemIds;
    private BigDecimal totalAmount;
}