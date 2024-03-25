package org.project.nuwabackend.nuwa.domain.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 14)
public class RefreshToken implements Serializable {

    @Id
    private String id;

    @Indexed
    private final String email;

    private String refreshToken;

    @Builder
    private RefreshToken(String id, String email, String refreshToken) {
        this.id = id;
        this.email = email;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken createRefreshTokenInfo(String email, String refreshToken) {
        return RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .refreshToken(refreshToken)
                .build();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
