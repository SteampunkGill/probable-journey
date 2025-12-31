package com.milktea.backend.dto;

import lombok.Data;

@Data
public class PrintPrinterDTO {
    private Long id;
    private Long storeId;
    private String storeName;
    private String name;
    private String sn;
    private String key;
    private String type;
    private Integer status;
}