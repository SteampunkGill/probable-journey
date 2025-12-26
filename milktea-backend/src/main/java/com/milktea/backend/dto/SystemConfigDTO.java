package com.milktea.backend.dto;

import lombok.Data;

@Data
public class SystemConfigDTO {
    private String configKey;
    private String configValue;
    private String description;
}