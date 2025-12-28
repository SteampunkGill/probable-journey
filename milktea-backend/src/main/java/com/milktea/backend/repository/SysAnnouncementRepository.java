package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.SysAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SysAnnouncementRepository extends JpaRepository<SysAnnouncement, Long> {
    
    @Query("SELECT a FROM SysAnnouncement a WHERE a.isActive = true AND " +
           "(a.startTime IS NULL OR a.startTime <= :now) AND " +
           "(a.endTime IS NULL OR a.endTime >= :now) " +
           "ORDER BY a.createdAt DESC")
    List<SysAnnouncement> findActiveAnnouncements(LocalDateTime now);
}