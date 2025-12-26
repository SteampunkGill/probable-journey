package com.milktea.backend.dto;

import lombok.Data;

@Data
public class AboutUsDTO {
    private String title;
    private String content;
    private String logoUrl;
    private String contactPhone;
    private String address;
    private Double latitude;
    private Double longitude;
}