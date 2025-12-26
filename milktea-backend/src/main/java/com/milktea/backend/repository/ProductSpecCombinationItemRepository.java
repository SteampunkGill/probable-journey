package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductSpecCombinationItem;
import com.milktea.milktea_backend.model.entity.ProductSpecCombinationItemId;

@Repository
public interface ProductSpecCombinationItemRepository extends JpaRepository<ProductSpecCombinationItem, ProductSpecCombinationItemId> {
}
