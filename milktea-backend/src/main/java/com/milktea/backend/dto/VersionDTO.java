package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionDTO {
    private String version;
    private String updateContent;
    private String downloadUrl;
    private Boolean forceUpdate;
}