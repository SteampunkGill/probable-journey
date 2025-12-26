package com.milktea.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeSalesDTO {
    private List<String> timeLabels;
    private List<BigDecimal> salesData;
    private List<Integer> orderData;
}