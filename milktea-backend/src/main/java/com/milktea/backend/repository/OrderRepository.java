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

    List<Order> findByUserId(Long userId);


    List<Order> findByUserIdOrderByOrderTimeDesc(Long userId);

    List<Order> findByStoreId(Long storeId);

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByStatus(String status);

    List<Order> findByUserIdAndStatus(Long userId, String status);

    List<Order> findByUserIdAndStatusOrderByOrderTimeDesc(Long userId, String status);
    

    List<Order> findByStoreIdAndStatus(Long storeId, String status);

    List<Order> findByPayMethod(String payMethod);

    List<Order> findByDeliveryType(String deliveryType);

    List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<Order> findByPayTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    

    List<Order> findByUserIdAndOrderTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    List<Order> findByStoreIdAndOrderTimeBetween(Long storeId, LocalDateTime startTime, LocalDateTime endTime);
    

    Optional<Order> findByTransactionId(String transactionId);
    

    List<Order> findByPickupCode(String pickupCode);

    List<Order> findByIsCommented(Boolean isCommented);
    

    long countByUserId(Long userId);

    long countByStoreId(Long storeId);

    @Query("SELECT SUM(o.actualAmount) FROM Order o WHERE o.user.id = :userId")
    BigDecimal sumActualAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(o.actualAmount) FROM Order o WHERE o.store.id = :storeId")
    BigDecimal sumActualAmountByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT o FROM Order o ORDER BY o.orderTime DESC")
    List<Order> findRecentOrders();

    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING_PAYMENT', 'PAID', 'MAKING', 'READY') ORDER BY o.orderTime ASC")
    List<Order> findPendingOrders();


    long countByStoreIdAndStatusInAndOrderTimeBefore(Long storeId, List<String> statuses, LocalDateTime orderTime);

    long countByStatus(String status);

    long countByStoreIdAndOrderTimeBetween(Long storeId, LocalDateTime start, LocalDateTime end);

    long countByStatusIn(List<String> statuses);

    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.orderTime >= :startTime")
    long countActiveUsers(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u WHERE u.id NOT IN " +
           "(SELECT DISTINCT o.user.id FROM Order o WHERE o.orderTime >= :cutoffTime)")
    long countChurnedUsers(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query(value = "SELECT COUNT(*) FROM (SELECT o.user_id FROM orders o " +
           "WHERE o.order_time >= :since " +
           "GROUP BY o.user_id " +
           "HAVING COUNT(o.id) >= :minFrequency AND SUM(o.actual_amount) >= :minMonetary) AS t", nativeQuery = true)
    long countCoreUsers(@Param("since") LocalDateTime since,
                        @Param("minFrequency") long minFrequency,
                        @Param("minMonetary") BigDecimal minMonetary);


    @Query(value = "SELECT COUNT(*) FROM (SELECT o.user_id FROM orders o " +
           "WHERE o.order_time >= :since " +
           "GROUP BY o.user_id " +
           "HAVING SUM(o.actual_amount) >= :minMonetary) AS t", nativeQuery = true)
    long countPotentialUsers(@Param("since") LocalDateTime since,
                             @Param("minMonetary") BigDecimal minMonetary);

    @Query(value = "SELECT " +
           "SUM(CASE WHEN total_spent >= 1000 THEN 1 ELSE 0 END) as highValue, " +
           "SUM(CASE WHEN total_spent >= 200 AND total_spent < 1000 THEN 1 ELSE 0 END) as potential, " +
           "SUM(CASE WHEN total_spent < 200 OR total_spent IS NULL THEN 1 ELSE 0 END) as risk " +
           "FROM (SELECT u.id as user_id, SUM(o.actual_amount) as total_spent " +
           "      FROM users u LEFT JOIN orders o ON u.id = o.user_id " +
           "      GROUP BY u.id) as user_spending", nativeQuery = true)
    Map<String, Long> countUserSegmentation();
}
