package org.project.nuwabackend.global.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"org.project.nuwabackend.nuwa.websocket.repository", "org.project.nuwabackend.nuwa.canvas.repository"})
public class MongoConfig {
}
