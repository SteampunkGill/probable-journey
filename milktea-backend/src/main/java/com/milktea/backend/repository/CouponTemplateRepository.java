package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    

    List<CouponTemplate> findByType(String type);
    

    List<CouponTemplate> findByEndTimeBefore(LocalDateTime endTime);

    List<CouponTemplate> findByStartTimeAfter(LocalDateTime startTime);

    List<CouponTemplate> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<CouponTemplate> findByTotalQuantityGreaterThan(Integer totalQuantity);

    @Query("SELECT ct FROM CouponTemplate ct WHERE ct.totalQuantity = -1 OR ct.issuedQuantity < ct.totalQuantity")
    List<CouponTemplate> findAvailableCouponTemplates();

    @Query("SELECT ct FROM CouponTemplate ct WHERE ct.endTime < ?1")
    List<CouponTemplate> findExpiredCouponTemplates(LocalDateTime currentTime);
}
