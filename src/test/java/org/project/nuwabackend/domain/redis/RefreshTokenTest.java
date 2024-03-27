package org.project.nuwabackend.domain.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.nuwa.domain.redis.RefreshToken;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("[Domain] RefreshToken Domain Test")
class RefreshTokenTest {

    String email = "email";
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    @Test
    @DisplayName("[Domain] Create RefreshToken Info")
    void createRefreshTokenInfo() {
        //given

        //when
        RefreshToken refreshTokenInfo = RefreshToken.createRefreshTokenInfo(
                email,
                accessToken,
                refreshToken);

        //then
        assertThat(refreshTokenInfo.getEmail()).isEqualTo(email);
        assertThat(refreshTokenInfo.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(refreshTokenInfo.getId()).isNotNull();
    }

//    @Test
//    @DisplayName("[Domain] Update Refresh Token")
//    void updateRefreshToken() {
//        //given
//        String newRefreshToken = "newRefreshToken";
//        RefreshToken refreshTokenInfo = RefreshToken.createRefreshTokenInfo(
//                email,
//                refreshToken);
//
//        //when
//        refreshTokenInfo.updateRefreshToken(newRefreshToken);
//
//        //then
//        assertThat(refreshTokenInfo.getRefreshToken()).isEqualTo(newRefreshToken);
//    }

}