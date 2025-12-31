package com.milktea.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PushTaskDTO {
    private Long id;
    private String title;
    private String content;
    private String targetType;
    private String targetValue;
    private LocalDateTime sentTime;
    private String status;
    private String pushType;
    private String triggerType;
    private String triggerCondition;
    private String imageUrl;
    private String linkUrl;
    private LocalDateTime scheduledTime;
}