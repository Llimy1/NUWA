package org.project.nuwabackend.repository.mongo;

import org.project.nuwabackend.domain.mongo.Canvas;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CanvasRepository extends MongoRepository<Canvas, String> {
}
