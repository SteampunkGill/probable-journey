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

    List<UserAddress> findByUserIdAndIsHistoryFalseOrderByIsDefaultDescCreatedAtDesc(Long userId);


    List<UserAddress> findByUserIdAndIsHistoryTrueOrderByLastUsedAtDesc(Long userId);


    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);


    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.user.id = ?1 AND a.isDefault = true")
    void resetDefaultAddressByUserId(Long userId);

    @Query("SELECT a FROM UserAddress a WHERE a.user.id = ?1 ORDER BY a.usedCount DESC")
    List<UserAddress> findTopNByUserIdOrderByUsedCountDesc(Long userId, Pageable pageable);
}
