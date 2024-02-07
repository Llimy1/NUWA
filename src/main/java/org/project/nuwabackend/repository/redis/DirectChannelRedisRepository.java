package org.project.nuwabackend.repository.redis;

import org.project.nuwabackend.domain.redis.DirectChannelRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DirectChannelRedisRepository extends CrudRepository<DirectChannelRedis, String> {

    Optional<DirectChannelRedis> findByEmail(String email);
}
