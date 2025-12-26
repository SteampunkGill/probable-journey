package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ComplaintHandleDTO {
    private String solution;
    private String remark;
    private BigDecimal compensation;
    private String compensationType;
    private Long compensationCouponId;
    private String status;
}