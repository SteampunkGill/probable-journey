package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.SysUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    

    Optional<SysUser> findByUsername(String username);
    

    boolean existsByUsername(String username);

    List<SysUser> findByStatus(String status);
    

    Optional<SysUser> findByEmail(String email);

    Optional<SysUser> findByPhone(String phone);
}
