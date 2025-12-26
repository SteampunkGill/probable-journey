package com.milktea.backend.dto;

import lombok.Data;

@Data
public class RefundReviewDTO {
    private String status; // APPROVED, REJECTED
    private String reply;
}