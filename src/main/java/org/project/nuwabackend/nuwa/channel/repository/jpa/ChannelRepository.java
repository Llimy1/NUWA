package org.project.nuwabackend.nuwa.channel.repository.jpa;

import org.project.nuwabackend.nuwa.domain.channel.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByRoomId(String roomId);
}
