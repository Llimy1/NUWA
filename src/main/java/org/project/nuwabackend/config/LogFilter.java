package org.project.nuwabackend.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        long startTime = System.currentTimeMillis();

        if (!req.getRequestURI().startsWith("/actuator")) {
            filterChain.doFilter(servletRequest, servletResponse);
            long duration = System.currentTimeMillis() - startTime;
            log.info("Request: " + req.getMethod() + " " + req.getRequestURI() + " | Response: " + res.getStatus() + " | Duration: " + duration + " ms");
        } else {
            // Actuator 요청의 경우 로그를 남기지 않고 필터 체인을 계속 진행
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
