package com.milktea.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private List<Long> permissionIds;
}