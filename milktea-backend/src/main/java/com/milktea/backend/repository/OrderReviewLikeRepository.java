package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.OrderReviewLike;
import com.milktea.milktea_backend.model.entity.OrderReviewLikeId;

@Repository
public interface OrderReviewLikeRepository extends JpaRepository<OrderReviewLike, OrderReviewLikeId> {
}
