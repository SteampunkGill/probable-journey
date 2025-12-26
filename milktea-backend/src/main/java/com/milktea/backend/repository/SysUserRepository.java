package com.milktea.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milktea.milktea_backend.model.entity.SysUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    
    /**
     * 根据用户名查找员工
     * @param username 用户名
     * @return 员工实体Optional
     */
    Optional<SysUser> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据状态查询员工
     * @param status 员工状态
     * @return 员工列表
     */
    List<SysUser> findByStatus(String status);
    
    /**
     * 根据邮箱查询员工
     * @param email 邮箱
     * @return 员工实体Optional
     */
    Optional<SysUser> findByEmail(String email);
    
    /**
     * 根据手机号查询员工
     * @param phone 手机号
     * @return 员工实体Optional
     */
    Optional<SysUser> findByPhone(String phone);
}
