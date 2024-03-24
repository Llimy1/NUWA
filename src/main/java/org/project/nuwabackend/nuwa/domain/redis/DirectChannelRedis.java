package org.project.nuwabackend.nuwa.domain.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Objects;

@Getter
@NoArgsConstructor
@RedisHash(value = "directChannel", timeToLive = 30)
public class DirectChannelRedis {

    @Id
    private String id;

    @Indexed
    private String directRoomId;
    @Indexed
    private String email;

    @Builder
    private DirectChannelRedis(String directRoomId, String email) {
        this.directRoomId = directRoomId;
        this.email = email;
    }

    public static DirectChannelRedis createDirectChannelRedis(String directRoomId, String email) {
        return DirectChannelRedis.builder()
                .directRoomId(directRoomId)
                .email(email)
                .build();
    }


}
