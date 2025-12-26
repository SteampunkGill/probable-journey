package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.IngredientRepository;
import com.milktea.backend.repository.SysOperationLogRepository;
import com.milktea.milktea_backend.model.entity.Ingredient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InventoryService {

    private final IngredientRepository ingredientRepository;
    private final SysOperationLogRepository sysOperationLogRepository;

    public InventoryService(IngredientRepository ingredientRepository,
                            SysOperationLogRepository sysOperationLogRepository) {
        this.ingredientRepository = ingredientRepository;
        this.sysOperationLogRepository = sysOperationLogRepository;
    }

    public List<Ingredient> findAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Transactional
    public Ingredient updateStock(Long id, BigDecimal stock) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ServiceException("INGREDIENT_NOT_FOUND", "原料不存在，ID: " + id));
        ingredient.setStock(stock);
        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public Ingredient updateAlertThreshold(Long id, BigDecimal threshold) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ServiceException("INGREDIENT_NOT_FOUND", "原料不存在，ID: " + id));
        ingredient.setAlertThreshold(threshold);
        return ingredientRepository.save(ingredient);
    }

    /**
     * 获取库存流水记录
     */
    public List<String> getInventoryRecords(Long id) {
        // 真实实现：从 sys_operation_log 中查询该原料的库存变更记录
        return sysOperationLogRepository.findAll().stream()
                .filter(log -> "INVENTORY".equals(log.getModule()) && log.getParamsJson().contains("\"id\":" + id))
                .map(log -> log.getCreatedAt() + " " + log.getAction() + " " + log.getParamsJson())
                .collect(java.util.stream.Collectors.toList());
    }
}