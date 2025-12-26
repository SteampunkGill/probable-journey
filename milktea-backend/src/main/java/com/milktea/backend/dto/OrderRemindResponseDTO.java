package com.milktea.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class OrderRemindResponseDTO {
    private Integer remindCount;
    private Integer maxRemindCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextRemindTime;
    
    private String message;
    private Boolean success;
}