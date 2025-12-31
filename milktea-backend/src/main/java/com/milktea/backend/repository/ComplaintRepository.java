package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStatus(String status);
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);
}