package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductSpecInventory;
import com.milktea.milktea_backend.model.entity.ProductSpecInventoryId;

@Repository
public interface ProductSpecInventoryRepository extends JpaRepository<ProductSpecInventory, ProductSpecInventoryId> {
}
