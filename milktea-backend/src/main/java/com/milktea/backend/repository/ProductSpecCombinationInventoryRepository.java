package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductSpecCombinationInventory;
import com.milktea.milktea_backend.model.entity.ProductSpecCombinationInventoryId;

@Repository
public interface ProductSpecCombinationInventoryRepository extends JpaRepository<ProductSpecCombinationInventory, ProductSpecCombinationInventoryId> {
}
