package org.project.nuwabackend.nuwa.channel.repository.redis;

import org.project.nuwabackend.nuwa.domain.redis.DirectChannelRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DirectChannelRedisRepository extends CrudRepository<DirectChannelRedis, String> {

    List<DirectChannelRedis> findByDirectRoomId(String directRoomId);
    Optional<DirectChannelRedis> findByDirectRoomIdAndEmail(String directRoomId, String email);
}
