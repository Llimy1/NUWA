package org.project.nuwabackend.nuwa.auth.repository.redis;

import org.project.nuwabackend.nuwa.domain.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByEmail(String email);

    Optional<RefreshToken> findByAccessToken(String accessToken);

}
