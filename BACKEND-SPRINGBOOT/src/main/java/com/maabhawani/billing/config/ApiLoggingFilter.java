package com.maabhawani.billing.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class ApiLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip logging for non-API routes (e.g., swagger UI assets) to avoid
        // unnecessary noise
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String method = request.getMethod();

        log.info("API IN: [{}] {}", method, requestURI);

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("API EXCEPTION: [{}] {} failed in {}ms with error: {}", method, requestURI, duration,
                    e.getMessage());
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            if (status >= 400 && status < 500) {
                log.warn("API CLIENT ERROR: [{}] {} completed with status {} in {}ms", method, requestURI, status,
                        duration);
            } else if (status >= 500) {
                log.error("API SERVER ERROR: [{}] {} completed with status {} in {}ms", method, requestURI, status,
                        duration);
            } else {
                log.info("API SUCCESS: [{}] {} completed with status {} in {}ms", method, requestURI, status, duration);
            }
        }
    }
}
