package org.project.nuwabackend.nuwa.workspacemember.repository;

import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkSpaceMember, Long> {

    Optional<WorkSpaceMember> findByName(String workSpaceMemberName);

    // 이메일과 워크스페이스 ID로 워크스페이스 멤버 찾기
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "JOIN wm.member m " +
            "JOIN wm.workSpace w " +
            "WHERE m.email = :email AND w.id = :workSpaceId AND wm.isDelete = false ")
    Optional<WorkSpaceMember> findByMemberEmailAndWorkSpaceId(@Param("email") String email, @Param("workSpaceId") Long workSpaceId);

    // 이메일과 워크스페이스 ID로 워크스페이스 멤버 찾기 -> 재참가에 사용
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "JOIN wm.member m " +
            "JOIN wm.workSpace w " +
            "WHERE m.email = :email AND w.id = :workSpaceId AND wm.isDelete = true ")
    Optional<WorkSpaceMember> findByDeleteMemberEmailAndWorkSpaceId(@Param("email") String email, @Param("workSpaceId") Long workSpaceId);

    List<WorkSpaceMember> findByMember(Member findMember);

    @Query("SELECT wsm.workSpace FROM WorkSpaceMember wsm WHERE wsm.member = :member")
    List<WorkSpace> findWorkSpacesByMember(@Param("member") Member member);

    // 워크스페이스멤버에서 조인으로 멤버랑 워크스페이스를 가져온다
    // 워크스페이스 레파지토리에서
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "JOIN wm.member m " +
            "JOIN FETCH wm.workSpace w " +
            "WHERE m.email = :email AND wm.isDelete = false")
    List<WorkSpaceMember> findByWorkSpaceList(@Param("email") String email);

    @Query("SELECT wsm FROM WorkSpaceMember wsm WHERE wsm.workSpace = :workSpace AND wsm.isDelete = false ")
    List<WorkSpaceMember> findByWorkSpace(@Param("workSpace") WorkSpace workSpace);

    // 워크스페이스 아이디로 워크스페이스에 해당되는 모든 멤버 가져오기
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "WHERE wm.workSpace.id = :workSpaceId AND wm.isDelete = false AND wm.id != :workSpaceMemberId")
    List<WorkSpaceMember> findListByWorkSpaceIdNot(@Param("workSpaceId") Long workSpaceId, @Param("workSpaceMemberId") Long workSpaceMemberId);

    // 채팅방의 참여한 멤버를 제외한 전체 워크스페이스 멤버
    @Query("SELECT wm " +
            "FROM WorkSpaceMember wm " +
            "WHERE wm.id NOT IN :joinMemberIdList AND wm.isDelete = false ")
    List<WorkSpaceMember> findListByNotJoinMember(@Param("joinMemberIdList") List<Long> joinMemberIdList);

    @Query("DELETE FROM WorkSpaceMember wm WHERE wm.workSpace.id = :workSpaceId")
    @Modifying(clearAutomatically = true)
    void deleteByWorkSpaceId(@Param("workSpaceId") Long workSpaceId);
}
