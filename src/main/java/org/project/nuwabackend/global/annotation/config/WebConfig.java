package org.project.nuwabackend.global.annotation.config;

import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.global.annotation.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.annotation.resolver.MemberEmailHandlerMethodArgumentResolver;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
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
