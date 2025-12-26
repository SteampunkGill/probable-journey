package com.milktea.milktea_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SysRolePermission {

    @EmbeddedId
    private SysRolePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private SysRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false)
    private SysPermission permission;
}
