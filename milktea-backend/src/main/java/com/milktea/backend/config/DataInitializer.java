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
    private final com.milktea.backend.repository.SystemConfigRepository systemConfigRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeWechatConfig();
        initializeSystemConfigs();
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

    private void initializeWechatConfig() {
        log.info("Checking wechat config initialization...");
        if (systemConfigRepository.findById("wechat_config").isEmpty()) {
            log.info("Initializing default wechat config...");
            com.milktea.milktea_backend.model.entity.SystemConfig config = new com.milktea.milktea_backend.model.entity.SystemConfig();
            config.setConfigKey("wechat_config");
            config.setConfigValue("{\"appId\":\"your_app_id\",\"secret\":\"your_app_secret\"}");
            config.setDescription("微信小程序配置");
            systemConfigRepository.save(config);
            log.info("Wechat config initialized.");
        }
    }

    private void initializeSystemConfigs() {
        log.info("Checking system configs initialization...");
        String[][] configs = {
            {"base_params", "{\"siteName\":\"奶茶点餐系统\",\"contactPhone\":\"400-123-4567\"}", "基础参数配置"},
            {"print_settings", "{\"autoPrint\":true,\"copies\":1}", "打印设置"}
        };

        for (String[] configData : configs) {
            if (systemConfigRepository.findById(configData[0]).isEmpty()) {
                log.info("Initializing config: {}", configData[0]);
                com.milktea.milktea_backend.model.entity.SystemConfig config = new com.milktea.milktea_backend.model.entity.SystemConfig();
                config.setConfigKey(configData[0]);
                config.setConfigValue(configData[1]);
                config.setDescription(configData[2]);
                systemConfigRepository.save(config);
            }
        }
    }
}