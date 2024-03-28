package org.project.nuwabackend.global.config.jpa;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.project.nuwabackend.nuwa",
excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "org.project.nuwabackend.nuwa.auth.repository.redis.*"),
        @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "org.project.nuwabackend.nuwa.canvas.repository.*"),
        @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "org.project.nuwabackend.nuwa.websocket.repository.*"),
        @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "org.project.nuwabackend.nuwa.auth.repository.redis.*"),
        @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "org.project.nuwabackend.nuwa.invite.repository.*"),
        @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "org.project.nuwabackend.nuwa.channel.repository.redis.*")
})
public class JpaConfig {
}
