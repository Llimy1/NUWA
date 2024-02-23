package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {

    Optional<WorkSpace> findByName(String workSpaceName);
}
