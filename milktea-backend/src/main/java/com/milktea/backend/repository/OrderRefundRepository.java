package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.OrderRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRefundRepository extends JpaRepository<OrderRefund, Long> {
    List<OrderRefund> findByStatus(String status);
}
