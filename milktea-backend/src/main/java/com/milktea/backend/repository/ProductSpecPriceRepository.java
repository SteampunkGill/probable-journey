package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductSpecPrice;
import com.milktea.milktea_backend.model.entity.ProductSpecPriceId;

@Repository
public interface ProductSpecPriceRepository extends JpaRepository<ProductSpecPrice, ProductSpecPriceId> {
}
