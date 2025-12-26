package com.milktea.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MemberDTO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String avatarUrl;
    private LocalDate birthday;
    private Long memberLevelId;
    private String memberLevelName;
    private Integer growthValue;
    private Integer points;
    private BigDecimal balance;
    private String memberCardNo;
    private String status;
    private LocalDateTime createdAt;
    private List<String> tags;
}