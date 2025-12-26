package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.PointTransaction;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    /**
     * 分页查询用户积分流水
     */
    org.springframework.data.domain.Page<PointTransaction> findByUserId(Long userId, org.springframework.data.domain.Pageable pageable);

    /**
     * 获取用户最新的一条积分流水
     */
    PointTransaction findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
