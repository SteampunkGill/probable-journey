package com.milktea.backend.config;

import com.milktea.backend.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

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
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}