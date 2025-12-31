package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.PointTransaction;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    org.springframework.data.domain.Page<PointTransaction> findByUserId(Long userId, org.springframework.data.domain.Pageable pageable);

    PointTransaction findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
