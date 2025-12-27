package com.milktea.backend.repository;

import com.milktea.milktea_backend.model.entity.SysBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysBackupRepository extends JpaRepository<SysBackup, Long> {
}