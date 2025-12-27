package com.milktea.backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserAddress;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    /**
     * 获取用户地址列表，按是否默认和创建时间排序
     */
    List<UserAddress> findByUserIdAndIsHistoryFalseOrderByIsDefaultDescCreatedAtDesc(Long userId);

    /**
     * 获取用户历史地址列表
     */
    List<UserAddress> findByUserIdAndIsHistoryTrueOrderByLastUsedAtDesc(Long userId);

    /**
     * 获取用户的默认地址
     */
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * 将用户的所有地址设为非默认
     */
    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.user.id = ?1 AND a.isDefault = true")
    void resetDefaultAddressByUserId(Long userId);

    /**
     * 获取地址历史记录（常用地址）
     * 使用 Pageable 来处理 LIMIT
     */
    @Query("SELECT a FROM UserAddress a WHERE a.user.id = ?1 ORDER BY a.usedCount DESC")
    List<UserAddress> findTopNByUserIdOrderByUsedCountDesc(Long userId, Pageable pageable);
}
