package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.multimedia.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByIdIn(List<Long> imageIdList);
}
