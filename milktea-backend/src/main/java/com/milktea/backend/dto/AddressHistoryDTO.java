package com.milktea.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddressHistoryDTO {
    private String address;
    private String detail;
    private Integer usedCount;
    private LocalDateTime lastUsedTime;
    private String tag;
}