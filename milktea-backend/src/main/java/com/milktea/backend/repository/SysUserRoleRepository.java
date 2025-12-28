package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.SysUserRole;
import com.milktea.milktea_backend.model.entity.SysUserRoleId;

@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, SysUserRoleId> {
    void deleteByUserId(Long userId);
}
