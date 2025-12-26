package com.milktea.backend.dto;

import lombok.Data;

@Data
public class GiftCardActivateRequest {
    private String cardNo;
    private String cardCode;
}