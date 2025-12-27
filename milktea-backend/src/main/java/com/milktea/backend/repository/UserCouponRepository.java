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
    
    /**
     * 查询用户未使用且未过期的优惠券
     * @param user 用户
     * @param endTime 结束时间
     * @return 用户优惠券列表
     */
    List<UserCoupon> findByUserAndUsedFalseAndEndTimeAfter(User user, LocalDateTime endTime);
    
    /**
     * 查询用户已使用的优惠券
     * @param user 用户
     * @return 用户优惠券列表
     */
    List<UserCoupon> findByUserAndUsedTrue(User user);
    
    /**
     * 查询用户已过期的优惠券
     * @param user 用户
     * @param currentTime 当前时间
     * @return 用户优惠券列表
     */
    List<UserCoupon> findByUserAndEndTimeBefore(User user, LocalDateTime currentTime);
    
    /**
     * 查询用户指定优惠券模板的优惠券
     * @param user 用户
     * @param couponTemplate 优惠券模板
     * @param used 是否已使用
     * @return 用户优惠券列表
     */
    List<UserCoupon> findByUserAndCouponTemplateAndUsed(User user, CouponTemplate couponTemplate, boolean used);
    
    /**
     * 查询在指定时间之后领取的优惠券
     * @param receiveTime 领取时间
     * @return 用户优惠券列表
     */
    List<UserCoupon> findByReceiveTimeAfter(LocalDateTime receiveTime);
    

    /**
     * 统计指定模板已使用的优惠券数量
     */
    long countByCouponTemplateIdAndUsedTrue(Long templateId);

    /**
     * 统计指定模板已领取的优惠券数量
     */
    long countByCouponTemplateId(Long templateId);
}
