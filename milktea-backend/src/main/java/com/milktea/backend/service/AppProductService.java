package com.milktea.backend.service;

import com.milktea.backend.dto.ProductFilterDTO;
import com.milktea.backend.repository.*;
import com.milktea.milktea_backend.model.entity.Category;
import com.milktea.milktea_backend.model.entity.OrderReview;
import com.milktea.milktea_backend.model.entity.Product;
import com.milktea.milktea_backend.model.entity.ProductOption;
import com.milktea.milktea_backend.model.entity.ProductSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductOptionRepository productOptionRepository;
    private final OrderReviewRepository orderReviewRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public List<Product> getProducts(Long categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndIsActive(categoryId, true);
        }
        return productRepository.findByIsActive(true);
    }

    public List<Product> advancedFilter(ProductFilterDTO filter) {
        // 简化实现，实际应使用 Specification 或 QueryDSL
        if (filter.getCategoryId() != null) {
            return productRepository.findByPriceRangeAndCategory(filter.getMinPrice(), filter.getMaxPrice(), filter.getCategoryId());
        }
        return productRepository.searchProducts(filter.getKeyword());
    }

    public Product getProductDetail(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<ProductSpec> getProductSpecs(Long productId) {
        return productSpecRepository.findByProductId(productId);
    }

    public List<ProductOption> getProductOptions(Long productId) {
        return productOptionRepository.findByProductId(productId);
    }

    public List<OrderReview> getProductReviews(Long productId) {
        return orderReviewRepository.findByProductId(productId);
    }
}