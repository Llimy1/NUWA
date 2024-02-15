package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkSpaceMember, Long> {

    Optional<WorkSpaceMember> findByName(String workSpaceMemberName);

    // 이메일로 워크스페이스 유저 찾기
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "JOIN wm.member m " +
            "WHERE m.email = :email")
    Optional<WorkSpaceMember> findByMemberEmail(@Param("email") String email);

    List<WorkSpaceMember> findByMember(Member findMember);

    @Query("SELECT wsm.workSpace FROM WorkSpaceMember wsm WHERE wsm.member = :member")
    List<WorkSpace> findWorkSpacesByMember(@Param("member") Member member);
    // 워크스페이스멤버에서 조인으로 멤버랑 워크스페이스를 가져온다
    // 워크스페이스 레파지토리에서
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "JOIN wm.member m " +
            "JOIN FETCH wm.workSpace w " +
            "WHERE m.email = :email ")
    List<WorkSpaceMember> findByWorkSpaceList(@Param("email") String email);
}
