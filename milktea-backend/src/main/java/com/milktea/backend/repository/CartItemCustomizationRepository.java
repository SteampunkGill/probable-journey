package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.CartItemCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemCustomizationRepository extends JpaRepository<CartItemCustomization, Long> {
}
