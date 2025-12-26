package com.milktea.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private Long parentId;
    private String name;
    private String iconUrl;
    private Integer sortOrder;
    private Boolean isActive;
    private List<CategoryDTO> children;
}