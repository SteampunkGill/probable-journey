package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.SysRolePermission;
import com.milktea.milktea_backend.model.entity.SysRolePermissionId;

@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, SysRolePermissionId> {
}
