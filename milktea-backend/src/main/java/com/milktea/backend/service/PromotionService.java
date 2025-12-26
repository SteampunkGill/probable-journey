package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.PromotionRepository;
import com.milktea.milktea_backend.model.entity.Promotion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<Promotion> getAllActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findAll().stream()
                .filter(p -> p.getIsActive() != null && p.getIsActive() && p.getStartTime().isBefore(now) && p.getEndTime().isAfter(now))
                .collect(Collectors.toList());
    }

    public List<Promotion> getPromotionsByType(String type) {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findAll().stream()
                .filter(p -> p.getType().equals(type) && p.getIsActive() != null && p.getIsActive() && p.getStartTime().isBefore(now) && p.getEndTime().isAfter(now))
                .collect(Collectors.toList());
    }

    public Promotion getPromotionDetail(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ServiceException("活动不存在"));
    }
}