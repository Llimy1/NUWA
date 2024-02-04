package org.project.nuwabackend.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.redis.RefreshToken;
import org.project.nuwabackend.dto.auth.GeneratedTokenDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.ErrorMessage.REFRESH_TOKEN_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public void saveTokenInfo(String email, String refreshToken) {
        log.info("Save Token Info Service 호출");
        refreshTokenRepository.save(RefreshToken.createRefreshTokenInfo(email, refreshToken));
    }

    public void removeRefreshToken(String accessToken) {
        log.info("Remove Refresh Token Service 호출");

        String email = jwtUtil.getEmail(accessToken);

        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(REFRESH_TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(refreshToken);
    }

    public String reissueToken(String accessToken) {
        log.info("Reissue Token Service 호출");

        String email = jwtUtil.getEmail(accessToken);
        String role = jwtUtil.getRole(accessToken);

        GeneratedTokenDto generatedToken = jwtUtil.generatedToken(email, role);

        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(REFRESH_TOKEN_NOT_FOUND));

        String newAccessToken = generatedToken.accessToken();
        String newRefreshToken = generatedToken.refreshToken();

        // Refresh Token Update
        refreshToken.updateRefreshToken(newRefreshToken);

        return newAccessToken;
    }
}
