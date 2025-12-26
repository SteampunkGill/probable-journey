package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductSpec;

@Repository
public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {
    java.util.List<ProductSpec> findByProductId(Long productId);
}
