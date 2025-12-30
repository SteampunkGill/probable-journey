package com.milktea.backend.config;

import com.milktea.backend.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("[DEBUG] Registering AuthInterceptor");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/v1/app/**")
                .excludePathPatterns("/api/v1/app/auth/**")
                .excludePathPatterns("/api/v1/app/home/**")
                .excludePathPatterns("/api/v1/app/banners/**")
                .excludePathPatterns("/api/v1/app/stores/**")
                .excludePathPatterns("/api/v1/app/products/**")
                .excludePathPatterns("/api/v1/app/categories")
                .excludePathPatterns("/api/v1/app/about/**");
    }

    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        // 确保路径以 file: 开头且以 / 结尾
        String location = uploadDir;
        if (!location.startsWith("file:") && !location.startsWith("classpath:")) {
            java.io.File file = new java.io.File(location);
            location = "file:" + file.getAbsolutePath() + "/";
        }
        if (!location.endsWith("/")) {
            location += "/";
        }
        System.out.println("[DEBUG] Setting resource location: " + location);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}