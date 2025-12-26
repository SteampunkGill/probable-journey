package com.milktea.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.milktea.backend",
    "com.milktea.milktea_backend"
})
@EntityScan(basePackages = {
    "com.milktea.backend",
    "com.milktea.milktea_backend.model.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.milktea.backend.repository"
})
public class MilkteaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilkteaBackendApplication.class, args);
    }
}