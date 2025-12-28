package com.milktea.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BackupDTO {
    private String id;
    private String fileName;
    private Long fileSize;
    private String status; // COMPLETED, FAILED, IN_PROGRESS
    private String downloadUrl;
    private LocalDateTime createdAt;
}