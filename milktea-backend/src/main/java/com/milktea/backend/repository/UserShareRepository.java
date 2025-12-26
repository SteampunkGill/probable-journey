package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.UserShare;

@Repository
public interface UserShareRepository extends JpaRepository<UserShare, Long> {
    /**
     * 获取邀请人的分享/邀请记录，按创建时间倒序
     */
    java.util.List<UserShare> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
