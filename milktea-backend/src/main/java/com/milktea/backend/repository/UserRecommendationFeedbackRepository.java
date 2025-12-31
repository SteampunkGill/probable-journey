package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserRecommendationFeedback;

@Repository
public interface UserRecommendationFeedbackRepository extends JpaRepository<UserRecommendationFeedback, Long> {

    long countByProductIdAndActionAndCreatedAtBetween(Long productId, String action, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
