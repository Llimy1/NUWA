package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkSpaceMember, Long> {

    Optional<WorkSpaceMember> findByName(String workSpaceMemberName);

    // 이메일로 워크스페이스 유저 찾기
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "JOIN wm.member m " +
            "WHERE m.email = :email")
    Optional<WorkSpaceMember> findByMemberEmail(@Param("email") String email);
}
