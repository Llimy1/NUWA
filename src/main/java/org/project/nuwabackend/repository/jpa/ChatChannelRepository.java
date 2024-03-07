package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatChannelRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByRoomId(String roomId);

    Slice<Chat> findByWorkSpaceId(Long workSpaceId, Pageable pageable);

    @Query("DELETE FROM Chat c WHERE c.workSpace.id = :workSpaceId")
    @Modifying(clearAutomatically = true)
    void deleteChatByWorkSpaceId(@Param("workSpaceId") Long workSpaceId);
}
