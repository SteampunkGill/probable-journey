package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.CouponTemplate;
import com.milktea.milktea_backend.model.entity.User;
import com.milktea.milktea_backend.model.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByUserAndCouponTemplate(User user, CouponTemplate couponTemplate);

    List<UserCoupon> findByUser(User user);

    List<UserCoupon> findByUserId(Long userId);

    List<UserCoupon> findByUserIdAndStatus(Long userId, String status);

    List<UserCoupon> findByUserAndUsedFalseAndEndTimeAfter(User user, LocalDateTime endTime);

    List<UserCoupon> findByUserAndUsedTrue(User user);
    

    List<UserCoupon> findByUserAndEndTimeBefore(User user, LocalDateTime currentTime);
    

    List<UserCoupon> findByUserAndCouponTemplateAndUsed(User user, CouponTemplate couponTemplate, boolean used);
    

    List<UserCoupon> findByReceiveTimeAfter(LocalDateTime receiveTime);

    long countByCouponTemplateIdAndUsedTrue(Long templateId);

    long countByCouponTemplateId(Long templateId);
}
