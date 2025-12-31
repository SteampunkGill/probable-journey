package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StoreDTO {
    private Long id;
    private String name;
    private String code;
    private String province;
    private String city;
    private String district;
    private String address;
    private String phone;
    private String managerName;
    private String managerPhone;
    private String status;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer deliveryRadius;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private Boolean isAutoAccept;
    private Boolean isOnlinePayment;
    private String businessHours;
    private List<BusinessHourDTO> businessHoursList;
    private String openTime;
    private String closeTime;
    private Integer businessStatus;
}