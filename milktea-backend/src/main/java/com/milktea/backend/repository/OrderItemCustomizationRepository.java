package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.OrderItemCustomization;

@Repository
public interface OrderItemCustomizationRepository extends JpaRepository<OrderItemCustomization, Long> {
}
