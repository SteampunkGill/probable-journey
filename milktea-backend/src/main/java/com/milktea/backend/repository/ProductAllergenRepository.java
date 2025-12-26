package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.ProductAllergen;
import com.milktea.milktea_backend.model.entity.ProductAllergenId;

@Repository
public interface ProductAllergenRepository extends JpaRepository<ProductAllergen, ProductAllergenId> {
}
