package com.milktea.backend.service;

import com.milktea.backend.exception.ServiceException;
import com.milktea.backend.repository.SysUserRepository;
import com.milktea.milktea_backend.model.entity.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SysUserService {

    private final SysUserRepository sysUserRepository;

    public SysUserService(SysUserRepository sysUserRepository) {
        this.sysUserRepository = sysUserRepository;
    }


    public List<SysUser> findAllSysUsers() {
        return sysUserRepository.findAll();
    }


    public Optional<SysUser> getSysUserById(Long id) {
        return sysUserRepository.findById(id);
    }


    @Transactional
    public SysUser createSysUser(SysUser sysUser) {
        // 验证输入
        if (sysUser == null) {
            throw new ServiceException("SYSUSER_REQUIRED", "员工信息不能为空");
        }
        if (!StringUtils.hasText(sysUser.getUsername())) {
            throw new ServiceException("USERNAME_REQUIRED", "用户名不能为空");
        }
        if (!StringUtils.hasText(sysUser.getPasswordHash())) {
            throw new ServiceException("PASSWORD_REQUIRED", "密码不能为空");
        }
        
        // 检查用户名是否已存在
        if (sysUserRepository.existsByUsername(sysUser.getUsername())) {
            throw new ServiceException("USERNAME_EXISTS", "用户名已存在");
        }
        // 检查邮箱是否已存在
        if (sysUser.getEmail() != null && sysUserRepository.findByEmail(sysUser.getEmail()).isPresent()) {
            throw new ServiceException("EMAIL_EXISTS", "邮箱已存在");
        }
        // 检查手机号是否已存在
        if (sysUser.getPhone() != null && sysUserRepository.findByPhone(sysUser.getPhone()).isPresent()) {
            throw new ServiceException("PHONE_EXISTS", "手机号已存在");
        }
        return sysUserRepository.save(sysUser);
    }


    @Transactional
    public Optional<SysUser> updateSysUser(Long id, SysUser sysUserDetails) {
        return sysUserRepository.findById(id).map(sysUser -> {
            // 验证输入
            if (sysUserDetails == null) {
                throw new ServiceException("SYSUSER_DETAILS_REQUIRED", "员工更新信息不能为空");
            }
            
            // 验证用户名是否已存在（排除当前员工）
            if (StringUtils.hasText(sysUserDetails.getUsername()) &&
                !sysUser.getUsername().equals(sysUserDetails.getUsername())) {
                if (sysUserRepository.existsByUsername(sysUserDetails.getUsername())) {
                    throw new ServiceException("USERNAME_EXISTS", "用户名已存在");
                }
                sysUser.setUsername(sysUserDetails.getUsername());
            }
            
            // 验证密码（如果不为空则更新）
            if (StringUtils.hasText(sysUserDetails.getPasswordHash())) {
                sysUser.setPasswordHash(sysUserDetails.getPasswordHash());
            }
            
            // 验证邮箱是否已存在（排除当前员工）
            if (sysUserDetails.getEmail() != null &&
                !sysUserDetails.getEmail().equals(sysUser.getEmail())) {
                if (sysUserRepository.findByEmail(sysUserDetails.getEmail()).isPresent()) {
                    throw new ServiceException("EMAIL_EXISTS", "邮箱已存在");
                }
                sysUser.setEmail(sysUserDetails.getEmail());
            }
            
            // 验证手机号是否已存在（排除当前员工）
            if (sysUserDetails.getPhone() != null &&
                !sysUserDetails.getPhone().equals(sysUser.getPhone())) {
                if (sysUserRepository.findByPhone(sysUserDetails.getPhone()).isPresent()) {
                    throw new ServiceException("PHONE_EXISTS", "手机号已存在");
                }
                sysUser.setPhone(sysUserDetails.getPhone());
            }
            
            // 更新其他信息
            if (sysUserDetails.getRealName() != null) {
                sysUser.setRealName(sysUserDetails.getRealName());
            }
            if (sysUserDetails.getAvatar() != null) {
                sysUser.setAvatar(sysUserDetails.getAvatar());
            }
            if (sysUserDetails.getStatus() != null) {
                sysUser.setStatus(sysUserDetails.getStatus());
            }
            if (sysUserDetails.getLastLoginTime() != null) {
                sysUser.setLastLoginTime(sysUserDetails.getLastLoginTime());
            }
            if (sysUserDetails.getLastLoginIp() != null) {
                sysUser.setLastLoginIp(sysUserDetails.getLastLoginIp());
            }
            
            return sysUserRepository.save(sysUser);
        });
    }

    @Transactional
    public void deleteSysUser(Long id) {
        if (!sysUserRepository.existsById(id)) {
            throw new ServiceException("SYSUSER_NOT_FOUND", "员工不存在");
        }
        sysUserRepository.deleteById(id);
    }

    public Optional<SysUser> findSysUserByUsername(String username) {
        return sysUserRepository.findByUsername(username);
    }


    public List<SysUser> findSysUsersByStatus(String status) {
        return sysUserRepository.findByStatus(status);
    }


    public Optional<SysUser> findSysUserByEmail(String email) {
        return sysUserRepository.findByEmail(email);
    }


    public Optional<SysUser> findSysUserByPhone(String phone) {
        return sysUserRepository.findByPhone(phone);
    }


    @Transactional
    public Optional<SysUser> updateLastLoginInfo(Long id, String loginIp) {
        return sysUserRepository.findById(id).map(sysUser -> {
            sysUser.setLastLoginTime(java.time.LocalDateTime.now());
            sysUser.setLastLoginIp(loginIp);
            return sysUserRepository.save(sysUser);
        });
    }


    public boolean existsSysUser(Long id) {
        return sysUserRepository.existsById(id);
    }
}