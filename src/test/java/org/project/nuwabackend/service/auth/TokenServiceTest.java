package org.project.nuwabackend.service.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.redis.RefreshToken;
import org.project.nuwabackend.dto.auth.GeneratedTokenDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.redis.RefreshTokenRepository;
import org.project.nuwabackend.type.Role;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.project.nuwabackend.global.type.ErrorMessage.REFRESH_TOKEN_NOT_FOUND;

@DisplayName("[Service] Token Service Test")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    TokenService tokenService;

    String email = "email";
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    @Test
    @DisplayName("[Service] Save Token Info Success")
    void saveTokenInfoSuccess() {
        //given
        RefreshToken token =
                RefreshToken.createRefreshTokenInfo(email, refreshToken);

        given(refreshTokenRepository.save(any()))
                .willReturn(token);

        //when
        tokenService.saveTokenInfo(email, refreshToken);

        //then
        verify(refreshTokenRepository).save(token);
    }

    @Test
    @DisplayName("[Service] Remove Refresh Token Success")
    void removeRefreshTokenSuccess() {
        //given
        RefreshToken token =
                RefreshToken.createRefreshTokenInfo(email, refreshToken);
//
//        given(jwtUtil.getEmail(anyString()))
//                .willReturn(email);
        given(refreshTokenRepository.findByEmail(anyString()))
                .willReturn(Optional.of(token));

        //when
        tokenService.removeRefreshToken(accessToken);

        //then
        verify(refreshTokenRepository).delete(token);
    }

    @Test
    @DisplayName("[Service] Remove Refresh Token Fail")
    void removeRefreshTokenFail() {
        //given

//        given(jwtUtil.getEmail(anyString()))
//                .willReturn(email);
        given(refreshTokenRepository.findByEmail(anyString()))
                .willThrow(new NotFoundException(REFRESH_TOKEN_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> tokenService.removeRefreshToken(email))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("[Service] Reissue Token Success")
    void reissueTokenSuccess() {
        //given
        String role = Role.USER.getKey();
        GeneratedTokenDto newGeneratedToken =
                GeneratedTokenDto.builder()
                        .accessToken("new" + accessToken)
                        .refreshToken("new" + refreshToken)
                        .build();

        RefreshToken token =
                RefreshToken.createRefreshTokenInfo(email, refreshToken);

        given(jwtUtil.getEmail(anyString()))
                .willReturn(email);
        given(jwtUtil.getRole(any()))
                .willReturn(role);
        given(jwtUtil.generatedToken(anyString(), anyString()))
                .willReturn(newGeneratedToken);
        given(refreshTokenRepository.findByEmail(anyString()))
                .willReturn(Optional.of(token));

        token.updateRefreshToken(newGeneratedToken.refreshToken());

        //when
        String newAccessToken = tokenService.reissueToken(accessToken);

        //then
        assertThat(newAccessToken).isEqualTo(newGeneratedToken.accessToken());
        assertThat(token.getRefreshToken()).isEqualTo(newGeneratedToken.refreshToken());
        verify(jwtUtil).getEmail(accessToken);
        verify(jwtUtil).getRole(accessToken);
        verify(jwtUtil).generatedToken(email, role);
        verify(refreshTokenRepository).findByEmail(email);
    }

    @Test
    @DisplayName("[Service] Reissue Token Fail")
    void reissueTokenFail() {
        //given

        given(jwtUtil.getEmail(anyString()))
                .willReturn(email);
        given(refreshTokenRepository.findByEmail(anyString()))
                .willThrow(new NotFoundException(REFRESH_TOKEN_NOT_FOUND));

        //when
        //then
        assertThatThrownBy(() -> tokenService.reissueToken(accessToken))
                .isInstanceOf(NotFoundException.class);
    }
}