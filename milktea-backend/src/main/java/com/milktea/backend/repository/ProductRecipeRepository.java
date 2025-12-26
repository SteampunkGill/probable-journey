package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductRecipe;
import com.milktea.milktea_backend.model.entity.ProductRecipeId;

@Repository
public interface ProductRecipeRepository extends JpaRepository<ProductRecipe, ProductRecipeId> {
    java.util.List<ProductRecipe> findByProductId(Long productId);
}
