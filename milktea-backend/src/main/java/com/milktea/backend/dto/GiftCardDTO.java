package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GiftCardDTO {
    private Long id;
    private String cardNo;
    private BigDecimal faceValue;
    private BigDecimal balance;
    private String status;
    private LocalDateTime expiryTime;
    private LocalDateTime createdAt;
}