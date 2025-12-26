package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 如果需要自定义查询，可以在这里添加
}
