package com.milktea.backend.config;

import com.milktea.backend.repository.SysRoleRepository;
import com.milktea.milktea_backend.model.entity.SysRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SysRoleRepository sysRoleRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        log.info("Checking roles initialization...");
        try {
            if (sysRoleRepository.findByCode("ADMIN").isEmpty()) {
                log.info("Initializing ADMIN role...");
                SysRole adminRole = new SysRole();
                adminRole.setCode("ADMIN");
                adminRole.setName("系统管理员");
                adminRole.setDescription("拥有系统最高权限");
                sysRoleRepository.save(adminRole);
                log.info("ADMIN role initialized.");
            } else {
                log.info("ADMIN role already exists.");
            }

            if (sysRoleRepository.findByCode("STAFF").isEmpty()) {
                log.info("Initializing STAFF role...");
                SysRole staffRole = new SysRole();
                staffRole.setCode("STAFF");
                staffRole.setName("普通员工");
                staffRole.setDescription("负责日常门店运营");
                sysRoleRepository.save(staffRole);
                log.info("STAFF role initialized.");
            }
        } catch (Exception e) {
            log.error("Failed to initialize roles: {}", e.getMessage(), e);
        }
    }
}