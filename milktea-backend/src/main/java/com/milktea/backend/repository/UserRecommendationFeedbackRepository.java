package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserRecommendationFeedback;

@Repository
public interface UserRecommendationFeedbackRepository extends JpaRepository<UserRecommendationFeedback, Long> {
    /**
     * 统计指定商品在指定时间范围内的特定行为次数
     */
    long countByProductIdAndActionAndCreatedAtBetween(Long productId, String action, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
