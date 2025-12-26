package com.milktea.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchStatusRequestDTO {
    private List<Long> ids;
    private Integer status; // 1:上架 0:下架
}