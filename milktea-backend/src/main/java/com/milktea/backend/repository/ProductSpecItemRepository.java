package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductSpecItem;

@Repository
public interface ProductSpecItemRepository extends JpaRepository<ProductSpecItem, Long> {
    java.util.List<ProductSpecItem> findBySpecId(Long specId);
}
