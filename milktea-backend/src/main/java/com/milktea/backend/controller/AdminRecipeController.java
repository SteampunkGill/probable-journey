package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.RecipeDTO;
import com.milktea.backend.service.RecipeService;
import com.milktea.milktea_backend.model.entity.Ingredient;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.ProductRecipe;
import com.milktea.milktea_backend.model.entity.ProductRecipeId;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/recipes")
public class AdminRecipeController {

    private final RecipeService recipeService;

    public AdminRecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<RecipeDTO>> getRecipesByProduct(@PathVariable Long productId) {
        List<ProductRecipe> recipes = recipeService.findRecipesByProductId(productId);
        List<RecipeDTO> dtos = recipes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(dtos);
    }

    @PostMapping
    public ApiResponse<RecipeDTO> saveRecipe(@RequestBody RecipeDTO recipeDTO) {
        ProductRecipe recipe = convertToEntity(recipeDTO);
        ProductRecipe saved = recipeService.saveRecipe(recipe);
        return ApiResponse.success(convertToDTO(saved));
    }

    @PostMapping("/product/{productId}")
    public ApiResponse<Void> updateProductRecipes(@PathVariable Long productId, @RequestBody List<RecipeDTO> dtos) {
        List<ProductRecipe> recipes = dtos.stream()
                .map(dto -> {
                    dto.setProductId(productId);
                    return convertToEntity(dto);
                })
                .collect(Collectors.toList());
        recipeService.updateProductRecipes(productId, recipes);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/product/{productId}/ingredient/{ingredientId}")
    public ApiResponse<Void> deleteRecipe(@PathVariable Long productId, @PathVariable Long ingredientId) {
        recipeService.deleteRecipe(productId, ingredientId);
        return ApiResponse.success(null);
    }

    private RecipeDTO convertToDTO(ProductRecipe recipe) {
        RecipeDTO dto = new RecipeDTO();
        dto.setProductId(recipe.getId().getProductId());
        dto.setIngredientId(recipe.getId().getIngredientId());
        if (recipe.getIngredient() != null) {
            dto.setIngredientName(recipe.getIngredient().getName());
            dto.setUnit(recipe.getIngredient().getUnit());
        }
        dto.setQuantity(recipe.getQuantity());
        return dto;
    }

    private ProductRecipe convertToEntity(RecipeDTO dto) {
        ProductRecipe recipe = new ProductRecipe();
        ProductRecipeId id = new ProductRecipeId(dto.getProductId(), dto.getIngredientId());
        recipe.setId(id);
        
        Product product = new Product();
        product.setId(dto.getProductId());
        recipe.setProduct(product);
        
        Ingredient ingredient = new Ingredient();
        ingredient.setId(dto.getIngredientId());
        recipe.setIngredient(ingredient);
        
        recipe.setQuantity(dto.getQuantity());
        return recipe;
    }
}