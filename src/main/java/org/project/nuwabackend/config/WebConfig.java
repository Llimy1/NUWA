package org.project.nuwabackend.config;

import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.global.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.resolver.MemberEmailHandlerMethodArgumentResolver;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomPageableHandlerMethodArgumentResolver());
        resolvers.add(new MemberEmailHandlerMethodArgumentResolver(jwtUtil));
    }
}
