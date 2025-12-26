package com.milktea.backend.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username;
    private String phone;
    private String password;
    private String role; // user or admin
    private String adminCode; // 管理员注册密令
}