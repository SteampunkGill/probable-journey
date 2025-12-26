package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderRefundDTO {
    private Long id;
    private String orderNo;
    private BigDecimal amount;
    private String reason;
    private String status;
    private String reply;
    private LocalDateTime createdAt;
    private Long orderId;
}