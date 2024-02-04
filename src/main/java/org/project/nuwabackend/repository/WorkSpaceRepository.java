package org.project.nuwabackend.repository;

import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {
}
