package org.project.nuwabackend.repository;

import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkSpaceMember, Long> {
}
