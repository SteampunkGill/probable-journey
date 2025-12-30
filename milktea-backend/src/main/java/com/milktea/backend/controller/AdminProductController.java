package com.milktea.backend.controller;

import com.milktea.backend.dto.AdminProductDTO;
import com.milktea.backend.dto.ApiResponse;
import com.milktea.backend.dto.BatchStatusRequestDTO;
import com.milktea.backend.service.ProductService;
import com.milktea.milktea_backend.model.entity.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<AdminProductDTO>> getProductList() {
        List<Product> products = productService.findAllProducts();
        List<AdminProductDTO> dtos = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(dtos);
    }

    @PostMapping
    public ApiResponse<AdminProductDTO> createProduct(@RequestBody AdminProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = productService.createProduct(product);
        return ApiResponse.success(convertToDTO(savedProduct));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminProductDTO> updateProduct(@PathVariable Long id, @RequestBody AdminProductDTO productDTO) {
        productDTO.setId(id);
        Product product = convertToEntity(productDTO);
        Product updatedProduct = productService.updateProduct(product);
        return ApiResponse.success(convertToDTO(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Integer status = body.get("status");
        productService.batchUpdateProductStatus(java.util.Collections.singletonList(id), status == 1);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-status")
    public ApiResponse<Void> batchUpdateStatus(@RequestBody BatchStatusRequestDTO request) {
        productService.batchUpdateProductStatus(request.getIds(), request.getStatus() == 1);
        return ApiResponse.success(null);
    }

    private AdminProductDTO convertToDTO(Product product) {
        AdminProductDTO dto = new AdminProductDTO();
        dto.setId(product.getId());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setPrice(product.getPrice());
        dto.setCostPrice(product.getCostPrice());  // 新增：成本价
        dto.setIsMemberExclusive(product.getIsMemberExclusive());
        dto.setMemberPrice(product.getMemberPrice());
        dto.setStock(product.getStock());
        dto.setAlertThreshold(product.getAlertThreshold());
        dto.setSales(product.getSales());
        dto.setStatus(product.getStatus());
        dto.setIsActive(product.getIsActive());
        dto.setSugarContent(product.getSugarContent());
        dto.setCalories(product.getCalories());
        dto.setDefaultSweetness(product.getDefaultSweetness());
        dto.setDefaultTemperature(product.getDefaultTemperature());
        dto.setSupportSweetness(product.getSupportSweetness());
        dto.setSupportTemperature(product.getSupportTemperature());
        return dto;
    }

    private Product convertToEntity(AdminProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setImageUrlLegacy(dto.getImageUrl()); // 同步填充旧字段
        product.setPrice(dto.getPrice());
        product.setCostPrice(dto.getCostPrice());  // 新增：成本价
        product.setIsMemberExclusive(dto.getIsMemberExclusive());
        product.setMemberPrice(dto.getMemberPrice());
        product.setStock(dto.getStock());
        product.setAlertThreshold(dto.getAlertThreshold());
        product.setSales(dto.getSales());
        product.setStatus(dto.getStatus());
        product.setIsActive(dto.getIsActive());
        product.setSugarContent(dto.getSugarContent());
        product.setCalories(dto.getCalories());
        product.setDefaultSweetness(dto.getDefaultSweetness());
        product.setDefaultTemperature(dto.getDefaultTemperature());
        product.setSupportSweetness(dto.getSupportSweetness());
        product.setSupportTemperature(dto.getSupportTemperature());
        
        if (dto.getCategoryId() != null) {
            com.milktea.milktea_backend.model.entity.Category category = new com.milktea.milktea_backend.model.entity.Category();
            category.setId(dto.getCategoryId());
            product.setCategory(category);
        }
        
        return product;
    }
}