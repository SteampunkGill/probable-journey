package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.ProductRecipeRepository;
import com.milktea.milktea_backend.model.entity.ProductRecipe;
import com.milktea.milktea_backend.model.entity.ProductRecipeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecipeService {

    private final ProductRecipeRepository productRecipeRepository;
    private final com.milktea.backend.repository.ProductRepository productRepository;

    public RecipeService(ProductRecipeRepository productRecipeRepository,
                         com.milktea.backend.repository.ProductRepository productRepository) {
        this.productRecipeRepository = productRecipeRepository;
        this.productRepository = productRepository;
    }

    public List<ProductRecipe> findRecipesByProductId(Long productId) {
        return productRecipeRepository.findByProductId(productId);
    }

    @Transactional
    public ProductRecipe saveRecipe(ProductRecipe recipe) {
        ProductRecipe saved = productRecipeRepository.save(recipe);
        updateProductCost(recipe.getProduct().getId());
        return saved;
    }

    @Transactional
    public void updateProductRecipes(Long productId, List<ProductRecipe> recipes) {
        // 删除旧配方
        List<ProductRecipe> oldRecipes = productRecipeRepository.findByProductId(productId);
        productRecipeRepository.deleteAll(oldRecipes);
        
        // 保存新配方
        if (recipes != null && !recipes.isEmpty()) {
            productRecipeRepository.saveAll(recipes);
        }
        
        // 更新成本
        updateProductCost(productId);
    }

    @Transactional
    public void deleteRecipe(Long productId, Long ingredientId) {
        ProductRecipeId id = new ProductRecipeId(productId, ingredientId);
        if (!productRecipeRepository.existsById(id)) {
            throw new ServiceException("RECIPE_NOT_FOUND", "配方不存在");
        }
        productRecipeRepository.deleteById(id);
        updateProductCost(productId);
    }

    private void updateProductCost(Long productId) {
        List<ProductRecipe> recipes = productRecipeRepository.findByProductId(productId);
        java.math.BigDecimal totalCost = recipes.stream()
            .map(r -> {
                java.math.BigDecimal cost = r.getIngredient().getCostPerUnit();
                return (cost != null ? cost : java.math.BigDecimal.ZERO).multiply(r.getQuantity());
            })
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        productRepository.findById(productId).ifPresent(product -> {
            product.setCostPrice(totalCost);
            productRepository.save(product);
        });
    }

    @Transactional
    public void updateAllProductsCostByIngredient(Long ingredientId) {
        List<ProductRecipe> recipes = productRecipeRepository.findAll().stream()
                .filter(r -> r.getIngredient().getId().equals(ingredientId))
                .collect(java.util.stream.Collectors.toList());
        
        java.util.Set<Long> productIds = recipes.stream()
                .map(r -> r.getProduct().getId())
                .collect(java.util.stream.Collectors.toSet());
        
        for (Long productId : productIds) {
            updateProductCost(productId);
        }
    }
}