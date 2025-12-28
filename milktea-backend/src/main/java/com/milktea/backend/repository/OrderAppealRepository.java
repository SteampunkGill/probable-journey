package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.OrderAppeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderAppealRepository extends JpaRepository<OrderAppeal, Long> {
    List<OrderAppeal> findByUserId(Long userId);
    List<OrderAppeal> findByStatus(String status);
}