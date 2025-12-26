package com.milktea.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StaffDTO {
    private Long id;
    private String username;
    private String password; // 仅用于创建
    private String realName;
    private String phone;
    private String email;
    private String status;
    private LocalDateTime lastLoginTime;
    private List<String> roles;
    private LocalDateTime createdAt;
}