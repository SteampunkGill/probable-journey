package com.milktea.backend.dto;

import lombok.Data;

@Data
public class BusinessHourDTO {
    private Integer dayOfWeek; // 1-7
    private String startTime; // HH:mm
    private String endTime; // HH:mm
    private Boolean isOpen;
}