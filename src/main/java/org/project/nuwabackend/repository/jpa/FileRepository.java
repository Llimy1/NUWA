package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.multimedia.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByIdIn(List<Long> fileIdList);

}
