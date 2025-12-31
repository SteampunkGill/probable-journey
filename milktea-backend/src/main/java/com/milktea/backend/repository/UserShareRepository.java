package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserShare;

@Repository
public interface UserShareRepository extends JpaRepository<UserShare, Long> {

    java.util.List<UserShare> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
