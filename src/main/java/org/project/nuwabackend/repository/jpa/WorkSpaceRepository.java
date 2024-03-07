package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {

    Optional<WorkSpace> findByName(String workSpaceName);
}
