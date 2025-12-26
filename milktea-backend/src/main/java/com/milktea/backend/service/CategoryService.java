package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.CategoryRepository;
import com.milktea.milktea_backend.model.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Category category) {
        if (category.getId() == null) {
            throw new ServiceException("CATEGORY_ID_REQUIRED", "分类ID不能为空");
        }
        Category existing = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new ServiceException("CATEGORY_NOT_FOUND", "分类不存在"));
        
        existing.setName(category.getName());
        existing.setIconUrl(category.getIconUrl());
        existing.setSortOrder(category.getSortOrder());
        existing.setIsActive(category.getIsActive());
        
        if (category.getParent() != null) {
            existing.setParent(category.getParent());
        }
        
        return categoryRepository.save(existing);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ServiceException("CATEGORY_NOT_FOUND", "分类不存在");
        }
        categoryRepository.deleteById(id);
    }
}