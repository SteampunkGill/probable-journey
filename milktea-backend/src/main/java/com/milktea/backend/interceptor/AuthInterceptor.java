package com.milktea.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("[DEBUG] AuthInterceptor - URI: " + request.getRequestURI());
        
        org.springframework.security.core.Authentication authentication =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated() &&
            authentication.getPrincipal() instanceof com.milktea.milktea_backend.model.entity.User) {
            userId = ((com.milktea.milktea_backend.model.entity.User) authentication.getPrincipal()).getId();
            System.out.println("[DEBUG] AuthInterceptor - Found user in SecurityContext: " + userId);
        }

        if (userId == null) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("mock_token_")) {
                try {
                    String[] parts = token.split("_");
                    if (parts.length >= 3) {
                        userId = Long.parseLong(parts[2]);
                        System.out.println("[DEBUG] AuthInterceptor - Found user in MockToken: " + userId);
                    }
                } catch (Exception ignored) {}
            }
        }

        if (userId == null) {
            userId = 1L;
            System.out.println("[DEBUG] AuthInterceptor - Using default fallback userId: 1");
        }
        
        request.setAttribute("userId", userId);
        return true;
    }
}