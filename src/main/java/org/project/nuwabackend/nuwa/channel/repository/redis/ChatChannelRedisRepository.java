package org.project.nuwabackend.nuwa.channel.repository.redis;


import org.project.nuwabackend.nuwa.domain.redis.ChatChannelRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ChatChannelRedisRepository extends CrudRepository<ChatChannelRedis, String> {
    Optional<ChatChannelRedis> findByChatRoomIdAndEmail(String chatRoomId, String email);

    List<ChatChannelRedis> findByChatRoomId(String chatChannelRoomId);
}
