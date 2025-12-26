package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    
    /**
     * 根据类型查询优惠券模板
     * @param type 类型
     * @return 优惠券模板列表
     */
    List<CouponTemplate> findByType(String type);
    
    /**
     * 查询在指定时间之前结束的优惠券模板
     * @param endTime 结束时间
     * @return 优惠券模板列表
     */
    List<CouponTemplate> findByEndTimeBefore(LocalDateTime endTime);
    
    /**
     * 查询在指定时间之后开始的优惠券模板
     * @param startTime 开始时间
     * @return 优惠券模板列表
     */
    List<CouponTemplate> findByStartTimeAfter(LocalDateTime startTime);
    
    /**
     * 查询在指定时间范围内的优惠券模板
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 优惠券模板列表
     */
    List<CouponTemplate> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询总数量大于指定值的优惠券模板
     * @param totalQuantity 总数量
     * @return 优惠券模板列表
     */
    List<CouponTemplate> findByTotalQuantityGreaterThan(Integer totalQuantity);
    
    /**
     * 查询还有库存的优惠券模板（已发行数量小于总数量）
     * @return 优惠券模板列表
     */
    @Query("SELECT ct FROM CouponTemplate ct WHERE ct.totalQuantity = -1 OR ct.issuedQuantity < ct.totalQuantity")
    List<CouponTemplate> findAvailableCouponTemplates();
    
    /**
     * 查询已过期的优惠券模板
     * @param currentTime 当前时间
     * @return 优惠券模板列表
     */
    @Query("SELECT ct FROM CouponTemplate ct WHERE ct.endTime < ?1")
    List<CouponTemplate> findExpiredCouponTemplates(LocalDateTime currentTime);
}
