package com.milktea.backend.service;

import com.milktea.backend.repository.OrderRepository;
import com.milktea.backend.repository.PromotionRepository;
import com.milktea.backend.repository.UserRecommendationFeedbackRepository;
import com.milktea.milktea_backend.model.entity.Order;
import com.milktea.milktea_backend.model.entity.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final PromotionRepository promotionRepository;
    private final OrderRepository orderRepository;
    private final UserRecommendationFeedbackRepository feedbackRepository;

    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    @Transactional
    public Promotion create(Promotion promotion) {
        if (promotion.getIsActive() == null) {
            promotion.setIsActive(true);
        }
        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion updateTime(Long id, LocalDateTime startTime, LocalDateTime endTime) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        if (startTime != null) {
            promotion.setStartTime(startTime);
        }
        if (endTime != null) {
            promotion.setEndTime(endTime);
        }
        return promotionRepository.save(promotion);
    }

    public Map<String, Object> getAnalysis(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        
        List<Order> orders = orderRepository.findByOrderTimeBetween(promotion.getStartTime(), promotion.getEndTime());
        
        long participantCount = orders.stream().map(o -> o.getUser().getId()).distinct().count();
        long orderCount = orders.size();
        java.math.BigDecimal totalDiscountAmount = orders.stream()
                .map(Order::getDiscountAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        double conversionRate = 0;
        if (participantCount > 0) {
            // 从数据库统计点击活动的人数（UserRecommendationFeedback 中 action 为 CLICK 的记录）
            long clickCount = feedbackRepository.countByProductIdAndActionAndCreatedAtBetween(
                    id, "CLICK", promotion.getStartTime(), promotion.getEndTime());
            if (clickCount > 0) {
                conversionRate = (double) participantCount / clickCount * 100;
            } else {
                // 如果没有点击记录但有参与记录，说明数据可能不完整，设为100%或根据业务逻辑处理
                conversionRate = 100.0;
            }
        }

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("activityId", id);
        analysis.put("name", promotion.getName());
        analysis.put("participantCount", participantCount);
        analysis.put("orderCount", orderCount);
        analysis.put("totalDiscountAmount", totalDiscountAmount);
        analysis.put("conversionRate", String.format("%.1f%%", conversionRate));
        
        return analysis;
    }

    @Transactional
    public Promotion updateStatus(Long id, Boolean isActive) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        promotion.setIsActive(isActive);
        return promotionRepository.save(promotion);
    }
}