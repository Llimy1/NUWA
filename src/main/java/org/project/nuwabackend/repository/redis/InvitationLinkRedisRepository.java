package org.project.nuwabackend.repository.redis;

import org.project.nuwabackend.domain.redis.InvitationLinkRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvitationLinkRedisRepository extends CrudRepository<InvitationLinkRedis, String> {

    Optional<InvitationLinkRedis> findTopByWorkSpaceIdOrderByTokenDesc(Long workSpaceId);
    Optional<InvitationLinkRedis> findFirstByToken(String token);

}
