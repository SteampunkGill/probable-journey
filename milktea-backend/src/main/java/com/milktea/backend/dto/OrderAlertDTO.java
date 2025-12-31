package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAlertDTO {
    private Long orderId;
    private String orderNo;
    private String type;
    private String message;
    private LocalDateTime alertTime;
}