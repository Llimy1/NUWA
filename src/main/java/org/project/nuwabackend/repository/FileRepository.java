package org.project.nuwabackend.repository;

import org.project.nuwabackend.domain.multimedia.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
