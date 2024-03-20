package org.project.nuwabackend.nuwa.canvas.repository;

import org.project.nuwabackend.nuwa.domain.mongo.Canvas;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CanvasRepository extends MongoRepository<Canvas, String> {
}
