package org.project.nuwabackend.nuwa.channel.repository.jpa;

import org.project.nuwabackend.nuwa.domain.channel.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatChannelRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByRoomId(String roomId);

    @Query("SELECT c " +
            "FROM Chat c " +
            "JOIN c.createMember cm " +
            "JOIN cm.member m " +
            "WHERE c.roomId = :roomId AND cm.workSpace.id = :workSpaceId ")
    Optional<Chat> findByRoomIdAndEmailAndWorkSpaceId(@Param("roomId") String roomId, @Param("workSpaceId") Long workSpaceId);

    @Query("SELECT c " +
            "FROM Chat c " +
            "WHERE c.workSpace.id = :workSpaceId AND c.roomId = :roomId ")
    Optional<Chat> findByWorkSpaceIdAndRoomId(@Param("workSpaceId") Long workSpaceId, @Param("roomId") String roomId);

    @Query("SELECT c " +
            "FROM Chat c " +
            "WHERE c.workSpace.id = :workSpaceId AND c.createMember.id = :workSpaceMemberId AND c.isCreateMemberDelete = false")
    List<Chat> findChatListByWorkSpaceMemberId(@Param("workSpaceId") Long workSpaceId, @Param("workSpaceMemberId") Long workSpaceMemberId);

    Slice<Chat> findByWorkSpaceId(Long workSpaceId, Pageable pageable);

    @Query("DELETE FROM Chat c WHERE c.workSpace.id = :workSpaceId")
    @Modifying(clearAutomatically = true)
    void deleteChatByWorkSpaceId(@Param("workSpaceId") Long workSpaceId);
}
