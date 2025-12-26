package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ComplaintDTO {
    private Long id;
    private Long userId;
    private String nickname;
    private Long orderId;
    private String orderNo;
    private String type;
    private String content;
    private String imagesJson;
    private String phone;
    private String status;
    private String solution;
    private String remark;
    private BigDecimal compensation;
    private String compensationType;
    private Long compensationCouponId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}