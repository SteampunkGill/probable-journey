package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDTO {
    private String url;
    private String thumbnailUrl;
    private Long size;
    private Integer width;
    private Integer height;
}