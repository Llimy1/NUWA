package org.project.nuwabackend.domain.redis;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "invitationLink", timeToLive = 60 * 60 * 24)
public class InvitationLinkRedis {

    @Id
    private String id;

    @Indexed
    private String token;


    private Long workSpaceId;
}
