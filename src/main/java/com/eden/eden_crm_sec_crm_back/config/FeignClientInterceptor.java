package com.eden.eden_crm_sec_crm_back.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor {
    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request != null) {
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    // Forward the Bearer token
                    requestTemplate.header("Authorization", authorizationHeader);
                }
                requestTemplate.header("X-SERVICE-TO-SERVICE", serviceName);
            }
        };
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
