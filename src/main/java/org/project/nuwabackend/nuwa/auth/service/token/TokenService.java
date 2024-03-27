package org.project.nuwabackend.nuwa.auth.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.response.type.ErrorMessage;
import org.project.nuwabackend.nuwa.domain.redis.RefreshToken;
import org.project.nuwabackend.nuwa.auth.dto.GeneratedTokenDto;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.auth.repository.redis.RefreshTokenRepository;
import org.project.nuwabackend.nuwa.notification.repository.memory.EmitterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.project.nuwabackend.global.response.type.ErrorMessage.REFRESH_TOKEN_EXPIRED;
import static org.project.nuwabackend.global.response.type.ErrorMessage.REFRESH_TOKEN_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public void saveTokenInfo(String email, String accessToken, String refreshToken) {
        log.info("Save Token Info Service 호출");
        refreshTokenRepository.save(RefreshToken.createRefreshTokenInfo(email, accessToken, refreshToken));
    }


    public void removeRefreshToken(String email) {
        log.info("Remove Refresh Token Service 호출");

        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(REFRESH_TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(refreshToken);
    }

    public String reissueToken(String accessToken, String email) {
        log.info("Reissue Token Service 호출");

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByAccessToken(accessToken);

        if (optionalRefreshToken.isPresent() && jwtUtil.verifyToken(optionalRefreshToken.get().getRefreshToken())) {
            RefreshToken resultToken = optionalRefreshToken.get();

            String refreshToken = resultToken.getRefreshToken();
            String role = jwtUtil.getRole(refreshToken);

            GeneratedTokenDto generatedToken = jwtUtil.generatedToken(email, role);
            String generatedAccessToken = generatedToken.accessToken();
            String generatedRefreshToken = generatedToken.refreshToken();

            refreshTokenRepository.delete(resultToken);

            RefreshToken saveToken = RefreshToken.createRefreshTokenInfo(email, generatedAccessToken, generatedRefreshToken);
            refreshTokenRepository.save(saveToken);

            return generatedAccessToken;
        } else {
            throw new IllegalStateException(REFRESH_TOKEN_EXPIRED.getMessage());
        }
    }
}
