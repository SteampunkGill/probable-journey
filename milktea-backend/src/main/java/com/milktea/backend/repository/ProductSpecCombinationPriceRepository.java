package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductSpecCombinationPrice;
import com.milktea.milktea_backend.model.entity.ProductSpecCombinationPriceId;

@Repository
public interface ProductSpecCombinationPriceRepository extends JpaRepository<ProductSpecCombinationPrice, ProductSpecCombinationPriceId> {
}
