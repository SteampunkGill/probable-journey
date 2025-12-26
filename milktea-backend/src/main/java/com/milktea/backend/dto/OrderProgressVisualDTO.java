package com.milktea.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderProgressVisualDTO {
    private List<ProgressStepDTO> steps;
    private Integer totalSteps;
    private Integer currentStepIndex;
    private Integer progressPercentage;

    @Data
    public static class ProgressStepDTO {
        private String step;
        private String name;
        private Boolean completed;
        private String time;
        private String icon;
        private String color;
        private Boolean current;
        private Integer progress;
    }
}