package com.milktea.backend.controller;

import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.InventoryDTO;
import com.milktea.backend.service.InventoryService;
import com.milktea.milktea_backend.model.entity.Ingredient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/inventory")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ApiResponse<List<InventoryDTO>> getInventoryList() {
        List<Ingredient> ingredients = inventoryService.findAllIngredients();
        List<InventoryDTO> dtos = ingredients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(dtos);
    }

    @PutMapping("/{id}/stock")
    public ApiResponse<InventoryDTO> updateStock(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal stock = body.get("stock");
        Ingredient updated = inventoryService.updateStock(id, stock);
        return ApiResponse.success(convertToDTO(updated));
    }

    @PutMapping("/{id}/alert")
    public ApiResponse<InventoryDTO> updateAlert(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal threshold = body.get("alertThreshold");
        Ingredient updated = inventoryService.updateAlertThreshold(id, threshold);
        return ApiResponse.success(convertToDTO(updated));
    }

    @PutMapping("/{id}/cost")
    public ApiResponse<InventoryDTO> updateCost(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal cost = body.get("costPerUnit");
        Ingredient updated = inventoryService.updateCost(id, cost);
        return ApiResponse.success(convertToDTO(updated));
    }

    @GetMapping("/{id}/records")
    public ApiResponse<List<String>> getRecords(@PathVariable Long id) {
        return ApiResponse.success(inventoryService.getInventoryRecords(id));
    }
private InventoryDTO convertToDTO(Ingredient ingredient) {
    InventoryDTO dto = new InventoryDTO();
    dto.setId(ingredient.getId());
    dto.setName(ingredient.getName());
    dto.setUnit(ingredient.getUnit());
    dto.setStock(ingredient.getStock());
    dto.setAlertThreshold(ingredient.getAlertThreshold());
    dto.setCostPerUnit(ingredient.getCostPerUnit());
    
    boolean isLow = false;
        if (ingredient.getStock() != null && ingredient.getAlertThreshold() != null) {
            isLow = ingredient.getStock().compareTo(ingredient.getAlertThreshold()) <= 0;
        }
        dto.setIsLowStock(isLow);
        
        return dto;
    }
}