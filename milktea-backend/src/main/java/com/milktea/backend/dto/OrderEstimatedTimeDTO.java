package com.milktea.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class OrderEstimatedTimeDTO {
    private Integer estimatedWaitTime;
    private Integer queuePosition;
    private String currentStep;
    private String nextStep;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedReadyTime;
    
    private Integer progressPercentage;
}