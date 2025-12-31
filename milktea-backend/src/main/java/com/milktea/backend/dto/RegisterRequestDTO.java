package com.milktea.backend.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username;
    private String phone;
    private String password;
    private String role;
    private String adminCode;
}