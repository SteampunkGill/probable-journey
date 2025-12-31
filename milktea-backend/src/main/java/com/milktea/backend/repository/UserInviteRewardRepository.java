package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.UserInviteReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInviteRewardRepository extends JpaRepository<UserInviteReward, Long> {
    List<UserInviteReward> findByInviterId(Long inviterId);
    boolean existsByInviteeId(Long inviteeId);
}