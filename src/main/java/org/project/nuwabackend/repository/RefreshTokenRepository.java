package org.project.nuwabackend.repository;

import org.project.nuwabackend.domain.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByEmail(String email);

}
