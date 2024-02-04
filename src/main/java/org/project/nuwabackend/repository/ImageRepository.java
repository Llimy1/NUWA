package org.project.nuwabackend.repository;

import org.project.nuwabackend.domain.multimedia.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
