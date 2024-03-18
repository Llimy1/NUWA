package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.ChatJoinMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatJoinMemberRepository extends JpaRepository<ChatJoinMember, Long> {

    List<ChatJoinMember> findByChatChannelId(Long chatChannelId);

    @Query("SELECT cj " +
            "FROM ChatJoinMember cj " +
            "WHERE cj.chatChannel.id = :chatChannelId AND cj.joinMember.id = :joinMemberId")
    Optional<ChatJoinMember> findByChatChannelIdAndJoinMemberId(@Param("chatChannelId") Long chatChannelId,
                                                                @Param("joinMemberId") Long joinMemberId);

    @Query("SELECT cj " +
            "FROM ChatJoinMember cj " +
            "JOIN cj.joinMember jm " +
            "WHERE cj.chatChannel.id = :chatChannelId AND jm.id = :workSpaceMemberId ")
    Optional<ChatJoinMember> findByChatChannelIdAndWorkSpaceMemberId(@Param("chatChannelId") Long chatChannelId,
                                                                     @Param("workSpaceMemberId") Long workSpaceMemberId);

    @Query("SELECT cj " +
            "FROM ChatJoinMember cj " +
            "JOIN cj.joinMember jm " +
            "WHERE jm.workSpace.id = :workSpaceId AND jm.id = :workSpaceMemberId AND cj.isJoinMemberDelete = false")
    List<ChatJoinMember> findChatJoinMemberListByWorkSpaceIdAndWorkSpaceMemberId(@Param("workSpaceId") Long workSpaceId,
                                                                                 @Param("workSpaceMemberId") Long workSpaceMemberId);

    @Query("SELECT cj " +
            "FROM ChatJoinMember cj " +
            "JOIN cj.chatChannel ch " +
            "WHERE ch.roomId = :roomId")
    List<ChatJoinMember> findByJoinMemberList(@Param("roomId") String roomId);

    @Query("SELECT cj " +
            "FROM ChatJoinMember cj " +
            "JOIN cj.joinMember jm " +
            "WHERE jm.workSpace.id = :workSpaceId ")
    List<ChatJoinMember> findByWorkSpaceId(@Param("workSpaceId") Long workSpaceId);
}
