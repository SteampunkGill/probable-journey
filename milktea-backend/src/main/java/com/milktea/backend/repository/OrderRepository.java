package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据用户ID查找订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据店铺ID查找订单
     * @param storeId 店铺ID
     * @return 订单列表
     */
    List<Order> findByStoreId(Long storeId);
    
    /**
     * 根据订单号查找订单
     * @param orderNo 订单号
     * @return 订单Optional
     */
    Optional<Order> findByOrderNo(String orderNo);
    
    /**
     * 根据状态查找订单
     * @param status 状态
     * @return 订单列表
     */
    List<Order> findByStatus(String status);
    
    /**
     * 根据用户ID和状态查找订单
     * @param userId 用户ID
     * @param status 状态
     * @return 订单列表
     */
    List<Order> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 根据店铺ID和状态查找订单
     * @param storeId 店铺ID
     * @param status 状态
     * @return 订单列表
     */
    List<Order> findByStoreIdAndStatus(Long storeId, String status);
    
    /**
     * 根据支付方式查找订单
     * @param payMethod 支付方式
     * @return 订单列表
     */
    List<Order> findByPayMethod(String payMethod);
    
    /**
     * 根据配送类型查找订单
     * @param deliveryType 配送类型
     * @return 订单列表
     */
    List<Order> findByDeliveryType(String deliveryType);
    
    /**
     * 根据订单时间范围查找订单
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据支付时间范围查找订单
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<Order> findByPayTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和订单时间范围查找订单
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<Order> findByUserIdAndOrderTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据店铺ID和订单时间范围查找订单
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<Order> findByStoreIdAndOrderTimeBetween(Long storeId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据交易ID查找订单
     * @param transactionId 交易ID
     * @return 订单Optional
     */
    Optional<Order> findByTransactionId(String transactionId);
    
    /**
     * 根据取餐码查找订单
     * @param pickupCode 取餐码
     * @return 订单列表
     */
    List<Order> findByPickupCode(String pickupCode);
    
    /**
     * 根据是否已评价查找订单
     * @param isCommented 是否已评价
     * @return 订单列表
     */
    List<Order> findByIsCommented(Boolean isCommented);
    
    /**
     * 统计用户订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计店铺订单数量
     * @param storeId 店铺ID
     * @return 订单数量
     */
    long countByStoreId(Long storeId);
    
    /**
     * 统计用户总消费金额
     * @param userId 用户ID
     * @return 总消费金额
     */
    @Query("SELECT SUM(o.actualAmount) FROM Order o WHERE o.user.id = :userId")
    BigDecimal sumActualAmountByUserId(@Param("userId") Long userId);
    
    /**
     * 统计店铺总销售额
     * @param storeId 店铺ID
     * @return 总销售额
     */
    @Query("SELECT SUM(o.actualAmount) FROM Order o WHERE o.store.id = :storeId")
    BigDecimal sumActualAmountByStoreId(@Param("storeId") Long storeId);
    
    /**
     * 查找最近N条订单
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o ORDER BY o.orderTime DESC")
    List<Order> findRecentOrders();
    
    /**
     * 查找待处理的订单（状态为待支付、待制作等）
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING_PAYMENT', 'PAID', 'MAKING', 'READY') ORDER BY o.orderTime ASC")
    List<Order> findPendingOrders();

    /**
     * 统计在指定时间之前，且处于指定状态列表中的订单数量
     */
    long countByStoreIdAndStatusInAndOrderTimeBefore(Long storeId, List<String> statuses, LocalDateTime orderTime);

    /**
     * 根据状态统计订单数量
     */
    long countByStatus(String status);

    /**
     * 统计某门店在指定时间段内的订单数量
     */
    long countByStoreIdAndOrderTimeBetween(Long storeId, LocalDateTime start, LocalDateTime end);

    /**
     * 统计指定状态列表中的订单数量
     */
    long countByStatusIn(List<String> statuses);

    /**
     * 统计活跃用户数（在指定时间内有订单的用户）
     */
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.orderTime >= :startTime")
    long countActiveUsers(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计流失用户数（最后一次订单在指定时间之前的用户，或者从未下单的用户）
     * 这里简化为：最后一次订单在指定时间之前的用户
     */
    @Query("SELECT COUNT(DISTINCT u.id) FROM User u WHERE u.id NOT IN " +
           "(SELECT DISTINCT o.user.id FROM Order o WHERE o.orderTime >= :cutoffTime)")
    long countChurnedUsers(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 按消费金额对用户进行分组统计
     */
    @Query(value = "SELECT " +
           "SUM(CASE WHEN total_spent >= 1000 THEN 1 ELSE 0 END) as highValue, " +
           "SUM(CASE WHEN total_spent >= 200 AND total_spent < 1000 THEN 1 ELSE 0 END) as potential, " +
           "SUM(CASE WHEN total_spent < 200 OR total_spent IS NULL THEN 1 ELSE 0 END) as risk " +
           "FROM (SELECT u.id as user_id, SUM(o.actual_amount) as total_spent " +
           "      FROM users u LEFT JOIN orders o ON u.id = o.user_id " +
           "      GROUP BY u.id) as user_spending", nativeQuery = true)
    Map<String, Long> countUserSegmentation();
}
