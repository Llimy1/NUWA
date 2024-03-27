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
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24)
public class RefreshToken implements Serializable {

    @Id
    private String id;

    @Indexed
    private final String email;

    @Indexed
    private final String accessToken;

    private final String refreshToken;

    @Builder
    private RefreshToken(String id, String email, String accessToken, String refreshToken) {
        this.id = id;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken createRefreshTokenInfo(String email, String accessToken, String refreshToken) {
        return RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
