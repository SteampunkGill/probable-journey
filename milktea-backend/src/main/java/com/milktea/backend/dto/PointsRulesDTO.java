package com.milktea.backend.dto;

import lombok.Data;

@Data
public class PointsRulesDTO {
    private Integer pointsPerYuan;
    private Integer maxPointsPerOrder;
    private Integer pointsToCashRate;
    private Integer minPointsToUse;
    private String rules;
}