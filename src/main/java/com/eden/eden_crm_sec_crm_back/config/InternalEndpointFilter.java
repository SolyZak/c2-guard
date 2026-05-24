package com.eden.eden_crm_sec_crm_back.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class InternalEndpointFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_SERVICES = Set.of(
            "eden-operators-management",
            "eden-crm-sec-crm",
            "eden-crm-api-gateway"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String serviceHeader = request.getHeader("X-SERVICE-TO-SERVICE");

        if (serviceHeader == null || !ALLOWED_SERVICES.contains(serviceHeader)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Forbidden: Invalid service-to-service header\"}");
            response.setContentType("application/json");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().contains("/internal/");
    }
}
