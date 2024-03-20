package org.project.nuwabackend.nuwa.channel.repository.jpa;

import org.project.nuwabackend.nuwa.domain.channel.Direct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface DirectChannelRepository extends JpaRepository<Direct, Long> {

    @Query("SELECT d " +
            "FROM Direct d " +
            "JOIN d.createMember cm " +
            "JOIN d.joinMember jm " +
            "WHERE cm.id = :workSpaceMemberId AND d.isCreateMemberDelete = false " +
            "OR jm.id = :workSpaceMemberId AND d.isJoinMemberDelete = false ")
    Slice<Direct> findDirectChannelByCreateMemberIdOrJoinMemberId(@Param("workSpaceMemberId") Long workSpaceMemberId, Pageable pageable);

    @Query("SELECT d " +
            "FROM Direct d " +
            "JOIN d.createMember cm " +
            "JOIN d.joinMember jm " +
            "WHERE cm.id = :workSpaceMemberId AND d.isCreateMemberDelete = false " +
            "OR jm.id = :workSpaceMemberId AND d.isJoinMemberDelete = false ")
    List<Direct> findDirectChannelListByCreateMemberIdOrJoinMemberId(@Param("workSpaceMemberId") Long workSpaceMemberId);

    @Query("SELECT d " +
            "FROM Direct d " +
            "JOIN d.createMember cm " +
            "JOIN d.joinMember jm " +
            "WHERE ((cm.id = :workSpaceMemberId AND d.isCreateMemberDelete = false) OR (jm.id = :workSpaceMemberId AND d.isJoinMemberDelete = false)) " +
            "AND (cm.name LIKE %:workSpaceMemberName% OR jm.name LIKE %:workSpaceMemberName%)")
    List<Direct> findSearchDirectChannelByCreateMemberIdOrJoinMemberId(@Param("workSpaceMemberId") Long workSpaceMemberId, @Param("workSpaceMemberName") String workSpaceMemberName);

    @Query("SELECT d " +
            "FROM Direct d " +
            "JOIN d.createMember cm " +
            "JOIN d.joinMember jm " +
            "WHERE (cm.id = :createMemberId OR jm.id = :createMemberId) " +
            "AND (cm.id = :joinMemberId OR jm.id = :joinMemberId)")
    Optional<Direct> findByCreateMemberIdOrJoinMemberId(@Param("createMemberId") Long createMemberId, @Param("joinMemberId") Long joinMemberId);

    Optional<Direct> findByWorkSpaceIdAndRoomId(Long workSpaceId, String roomId);

    Optional<Direct> findByRoomId(String roomId);

    @Query("DELETE FROM Direct d WHERE d.workSpace.id = :workSpaceId")
    @Modifying(clearAutomatically = true)
    void deleteDirectByWorkSpaceId(@Param("workSpaceId") Long workSpaceId);

    @Query("SELECT d " +
            "FROM Direct d " +
            "JOIN d.workSpace w " +
            "WHERE w.id = :workSpaceId AND d.roomId = :roomId")
    Optional<Direct> findByWorkSpaceIdAndRoomIdAndEmail(@Param("workSpaceId") Long workSpaceId, @Param("roomId") String roomId);
}
