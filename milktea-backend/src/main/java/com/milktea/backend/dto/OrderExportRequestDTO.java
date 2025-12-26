package com.milktea.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderExportRequestDTO {
    private String startDate;
    private String endDate;
    private List<String> exportFields;
    private String format; // EXCEL, CSV, PDF
    private Long storeId;
}