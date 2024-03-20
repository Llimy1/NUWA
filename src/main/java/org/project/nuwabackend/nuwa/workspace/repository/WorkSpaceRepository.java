package org.project.nuwabackend.nuwa.workspace.repository;

import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {

    Optional<WorkSpace> findByName(String workSpaceName);
}
