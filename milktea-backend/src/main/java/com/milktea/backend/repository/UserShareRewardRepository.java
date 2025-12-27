package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.UserShareReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserShareRewardRepository extends JpaRepository<UserShareReward, Long> {
    List<UserShareReward> findByUserId(Long userId);
}