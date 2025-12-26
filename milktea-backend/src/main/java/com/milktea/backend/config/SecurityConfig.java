package com.milktea.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(org.springframework.security.config.Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/app/auth/**").permitAll()
                .requestMatchers("/api/v1/app/home/**").permitAll()
                .requestMatchers("/api/v1/app/banners/**").permitAll()
                .requestMatchers("/api/v1/app/stores/**").permitAll()
                .requestMatchers("/api/v1/app/products/**").permitAll()
                .requestMatchers("/api/v1/app/categories").permitAll()
                .requestMatchers("/api/common/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/error").permitAll()
                // 需要认证的接口
                .requestMatchers("/api/v1/app/address/**").authenticated()
                .requestMatchers("/api/v1/app/user/**").authenticated()
                .requestMatchers("/api/v1/app/order/**").authenticated()
                .requestMatchers("/api/v1/app/member/**").authenticated()
                .requestMatchers("/api/v1/app/favorites/**").authenticated()
                .requestMatchers("/api/v1/app/gift-card/**").authenticated()
                .requestMatchers("/api/v1/app/about").permitAll()
                .requestMatchers("/api/v1/app/about/**").permitAll()
                // 管理后台接口权限控制
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}