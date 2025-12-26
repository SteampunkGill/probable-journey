package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.CategoryDTO;
import com.milktea.backend.service.CategoryService;
import com.milktea.milktea_backend.model.entity.Category;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDTO> getCategory(@PathVariable Long id) {
        Category category = categoryService.findAllCategories().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new com.milktea.backend.exception.ServiceException("CATEGORY_NOT_FOUND", "分类不存在"));
        return ApiResponse.success(convertToDTO(category));
    }

    @GetMapping
    public ApiResponse<List<CategoryDTO>> getCategoryList() {
        List<Category> categories = categoryService.findAllCategories();
        List<CategoryDTO> dtos = categories.stream()
                .filter(this::isTopLevel)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(dtos);
    }

    private boolean isTopLevel(Category c) {
        if (c.getParent() == null) return true;
        try {
            // 处理数据库中 parent_id 为 0 的情况
            return c.getParent().getId() == null || c.getParent().getId() == 0;
        } catch (Exception e) {
            return true;
        }
    }

    @PostMapping
    public ApiResponse<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = convertToEntity(categoryDTO);
        Category saved = categoryService.createCategory(category);
        return ApiResponse.success(convertToDTO(saved));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(id);
        Category category = convertToEntity(categoryDTO);
        Category updated = categoryService.updateCategory(category);
        return ApiResponse.success(convertToDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }
        dto.setName(category.getName());
        dto.setIconUrl(category.getIconUrl());
        dto.setSortOrder(category.getSortOrder());
        dto.setIsActive(category.getIsActive());
        
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            dto.setChildren(category.getChildren().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    private Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setIconUrl(dto.getIconUrl());
        category.setSortOrder(dto.getSortOrder());
        category.setIsActive(dto.getIsActive());
        
        if (dto.getParentId() != null) {
            Category parent = new Category();
            parent.setId(dto.getParentId());
            category.setParent(parent);
        }
        
        return category;
    }
}