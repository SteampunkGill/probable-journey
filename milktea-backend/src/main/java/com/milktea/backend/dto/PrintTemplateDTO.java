package com.milktea.backend.dto;

import lombok.Data;

@Data
public class PrintTemplateDTO {
    private Long id;
    private String name;
    private String content;
    private Boolean isDefault;
}