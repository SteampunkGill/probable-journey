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
    

    List<Store> findByNameContaining(String name);

    List<Store> findByNameContainingIgnoreCase(String name);
    

    Optional<Store> findByCode(String code);

    List<Store> findByStatus(String status);
    

    List<Store> findByStatusIgnoreCase(String status);
    

    List<Store> findByAddressContaining(String address);
    

    List<Store> findByAddressContainingIgnoreCase(String address);

    List<Store> findByPhone(String phone);

    List<Store> findByPhoneContaining(String phone);
    

    List<Store> findByManagerNameContaining(String managerName);

    List<Store> findByManagerPhone(String managerPhone);
    

    List<Store> findByIsAutoAccept(Boolean isAutoAccept);
    

    List<Store> findByIsOnlinePayment(Boolean isOnlinePayment);
    

    @Query("SELECT s FROM Store s WHERE s.latitude BETWEEN :minLat AND :maxLat AND s.longitude BETWEEN :minLng AND :maxLng")
    List<Store> findByLocationRange(@Param("minLat") BigDecimal minLat, 
                                   @Param("maxLat") BigDecimal maxLat,
                                   @Param("minLng") BigDecimal minLng,
                                   @Param("maxLng") BigDecimal maxLng);
    

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


    @Query(value = "SELECT s.*, " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
           "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
           "sin(radians(s.latitude)))) AS distance " +
           "FROM stores s " +
           "WHERE s.status = 'OPEN' " +
           "ORDER BY distance", nativeQuery = true)
    List<Store> findAllOpenStoresOrderByDistance(@Param("latitude") BigDecimal latitude,
                                                @Param("longitude") BigDecimal longitude);

    List<Store> findByDeliveryRadiusGreaterThanEqual(Integer deliveryRadius);
    

    List<Store> findByDeliveryFeeBetween(BigDecimal minFee, BigDecimal maxFee);

    List<Store> findByMinOrderAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    

    @Query("SELECT s FROM Store s WHERE s.deliveryRadius > 0")
    List<Store> findDeliveryStores();
    

    @Query("SELECT s FROM Store s WHERE s.status = 'OPEN'")
    List<Store> findOpenStores();
    

    @Query("SELECT s FROM Store s ORDER BY s.createdAt DESC")
    List<Store> findRecentStores();

    long count();

    long countByStatus(String status);
    

    @Query("SELECT COUNT(s) FROM Store s WHERE s.status = 'OPEN'")
    long countOpenStores();

    @Query("SELECT COUNT(s) FROM Store s WHERE s.deliveryRadius > 0")
    long countDeliveryStores();
    

    List<Store> findByNameContainingAndStatus(String name, String status);
    

    List<Store> findByNameContainingIgnoreCaseAndStatusIgnoreCase(String name, String status);


    List<Store> findByAddressContainingAndStatus(String address, String status);


    List<Store> findByProvinceAndStatus(String province, String status);

    List<Store> findByCityAndStatus(String city, String status);


    List<Store> findByDistrictAndStatus(String district, String status);
}
