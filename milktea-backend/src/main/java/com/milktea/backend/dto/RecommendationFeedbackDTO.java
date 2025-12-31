package com.milktea.backend.dto;

import lombok.Data;

@Data
public class RecommendationFeedbackDTO {
    private Long productId;
    private String action;
}