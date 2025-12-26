package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.OrderReview;

import java.util.List;

@Repository
public interface OrderReviewRepository extends JpaRepository<OrderReview, Long> {
    List<OrderReview> findByProductId(Long productId);
}
