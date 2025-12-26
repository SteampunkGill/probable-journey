package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    /**
     * 根据门店名称模糊查询
     * @param name 门店名称
     * @return 门店列表
     */
    List<Store> findByNameContaining(String name);
    
    /**
     * 根据门店名称模糊查询（忽略大小写）
     * @param name 门店名称
     * @return 门店列表
     */
    List<Store> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据门店编码查找
     * @param code 门店编码
     * @return 门店
     */
    Optional<Store> findByCode(String code);
    
    /**
     * 根据状态查找门店
     * @param status 状态
     * @return 门店列表
     */
    List<Store> findByStatus(String status);
    
    /**
     * 根据状态查找门店（忽略大小写）
     * @param status 状态
     * @return 门店列表
     */
    List<Store> findByStatusIgnoreCase(String status);
    
    /**
     * 根据地址模糊查询
     * @param address 地址
     * @return 门店列表
     */
    List<Store> findByAddressContaining(String address);
    
    /**
     * 根据地址模糊查询（忽略大小写）
     * @param address 地址
     * @return 门店列表
     */
    List<Store> findByAddressContainingIgnoreCase(String address);
    
    /**
     * 根据电话号码查找
     * @param phone 电话号码
     * @return 门店列表
     */
    List<Store> findByPhone(String phone);
    
    /**
     * 根据电话号码模糊查询
     * @param phone 电话号码
     * @return 门店列表
     */
    List<Store> findByPhoneContaining(String phone);
    
    /**
     * 根据经理姓名查找
     * @param managerName 经理姓名
     * @return 门店列表
     */
    List<Store> findByManagerNameContaining(String managerName);
    
    /**
     * 根据经理电话查找
     * @param managerPhone 经理电话
     * @return 门店列表
     */
    List<Store> findByManagerPhone(String managerPhone);
    
    /**
     * 根据是否自动接单查找
     * @param isAutoAccept 是否自动接单
     * @return 门店列表
     */
    List<Store> findByIsAutoAccept(Boolean isAutoAccept);
    
    /**
     * 根据是否支持在线支付查找
     * @param isOnlinePayment 是否支持在线支付
     * @return 门店列表
     */
    List<Store> findByIsOnlinePayment(Boolean isOnlinePayment);
    
    /**
     * 根据经纬度范围查找门店
     * @param minLat 最小纬度
     * @param maxLat 最大纬度
     * @param minLng 最小经度
     * @param maxLng 最大经度
     * @return 门店列表
     */
    @Query("SELECT s FROM Store s WHERE s.latitude BETWEEN :minLat AND :maxLat AND s.longitude BETWEEN :minLng AND :maxLng")
    List<Store> findByLocationRange(@Param("minLat") BigDecimal minLat, 
                                   @Param("maxLat") BigDecimal maxLat,
                                   @Param("minLng") BigDecimal minLng,
                                   @Param("maxLng") BigDecimal maxLng);
    
    /**
     * 查找附近门店（根据经纬度和半径）
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 半径（公里）
     * @return 门店列表
     */
    @Query(value = "SELECT s.*, " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
           "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
           "sin(radians(s.latitude)))) AS distance " +
           "FROM stores s " +
           "HAVING distance <= :radius " +
           "ORDER BY distance", nativeQuery = true)
    List<Store> findNearbyStores(@Param("latitude") BigDecimal latitude,
                                @Param("longitude") BigDecimal longitude,
                                @Param("radius") Double radius);

    /**
     * 查找所有营业中的门店并按距离排序
     * @param latitude 纬度
     * @param longitude 经度
     * @return 门店列表
     */
    @Query(value = "SELECT s.*, " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
           "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
           "sin(radians(s.latitude)))) AS distance " +
           "FROM stores s " +
           "WHERE s.status = 'OPEN' " +
           "ORDER BY distance", nativeQuery = true)
    List<Store> findAllOpenStoresOrderByDistance(@Param("latitude") BigDecimal latitude,
                                                @Param("longitude") BigDecimal longitude);
    
    /**
     * 根据配送半径查找门店
     * @param deliveryRadius 配送半径
     * @return 门店列表
     */
    List<Store> findByDeliveryRadiusGreaterThanEqual(Integer deliveryRadius);
    
    /**
     * 根据配送费范围查找门店
     * @param minFee 最小配送费
     * @param maxFee 最大配送费
     * @return 门店列表
     */
    List<Store> findByDeliveryFeeBetween(BigDecimal minFee, BigDecimal maxFee);
    
    /**
     * 根据最低订单金额范围查找门店
     * @param minAmount 最低订单金额
     * @param maxAmount 最高订单金额
     * @return 门店列表
     */
    List<Store> findByMinOrderAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * 查找支持配送的门店
     * @return 门店列表
     */
    @Query("SELECT s FROM Store s WHERE s.deliveryRadius > 0")
    List<Store> findDeliveryStores();
    
    /**
     * 查找营业中的门店
     * @return 门店列表
     */
    @Query("SELECT s FROM Store s WHERE s.status = 'OPEN'")
    List<Store> findOpenStores();
    
    /**
     * 查找最近创建的门店
     * @return 门店列表
     */
    @Query("SELECT s FROM Store s ORDER BY s.createdAt DESC")
    List<Store> findRecentStores();
    
    /**
     * 统计门店数量
     * @return 门店总数
     */
    long count();
    
    /**
     * 根据状态统计门店数量
     * @param status 状态
     * @return 门店数量
     */
    long countByStatus(String status);
    
    /**
     * 统计营业中的门店数量
     * @return 门店数量
     */
    @Query("SELECT COUNT(s) FROM Store s WHERE s.status = 'OPEN'")
    long countOpenStores();
    
    /**
     * 统计支持配送的门店数量
     * @return 门店数量
     */
    @Query("SELECT COUNT(s) FROM Store s WHERE s.deliveryRadius > 0")
    long countDeliveryStores();
    
    /**
     * 根据名称和状态查找门店
     * @param name 门店名称
     * @param status 状态
     * @return 门店列表
     */
    List<Store> findByNameContainingAndStatus(String name, String status);
    
    /**
     * 根据名称和状态查找门店（忽略大小写）
     * @param name 门店名称
     * @param status 状态
     * @return 门店列表
     */
    List<Store> findByNameContainingIgnoreCaseAndStatusIgnoreCase(String name, String status);
    
    /**
     * 根据地址和状态查找门店
     * @param address 地址
     * @param status 状态
     * @return 门店列表
     */
    List<Store> findByAddressContainingAndStatus(String address, String status);
}
