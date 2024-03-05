package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatChannelRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByRoomId(String roomId);

    Slice<Chat> findByWorkSpaceId(Long workSpaceId, Pageable pageable);
}
