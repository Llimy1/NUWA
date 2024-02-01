package org.project.nuwabackend.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.exception.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.info("JwtExceptionFilter accessToken = {}", request.getHeader("Authorization"));
            log.warn("Token 값이 올바르지 않습니다.");
            response.setCharacterEncoding("utf-8");
            response.sendError(401, "Token이 올바르지 않습니다.");
        }
    }
}
