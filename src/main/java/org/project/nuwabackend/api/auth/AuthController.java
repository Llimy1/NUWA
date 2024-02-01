package org.project.nuwabackend.api.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.auth.request.LoginRequestDto;
import org.project.nuwabackend.dto.auth.GeneratedTokenDto;
import org.project.nuwabackend.dto.auth.response.AccessTokenResponse;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.auth.LoginService;
import org.project.nuwabackend.service.auth.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.LOGIN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.LOGOUT_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.REISSUE_TOKEN_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final GlobalService globalService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("Login API 호출");
        GeneratedTokenDto tokenDto = loginService.login(loginRequestDto);

        String email = loginRequestDto.email();
        String accessToken = tokenDto.accessToken();
        String refreshToken = tokenDto.refreshToken();

        log.info("Redis에 TokenInfo 저장");
        tokenService.saveTokenInfo(email, refreshToken);

        GlobalSuccessResponseDto<Object> loginSuccessResponse =
                globalService.successResponse(LOGIN_SUCCESS.getMessage(), new AccessTokenResponse(accessToken));

        return ResponseEntity.status(CREATED).body(loginSuccessResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String accessToken) {
        log.info("Logout API 호출");
        tokenService.removeRefreshToken(accessToken);

        GlobalSuccessResponseDto<Object> logoutSuccessResponse =
                globalService.successResponse(LOGOUT_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(logoutSuccessResponse);
    }

    @PostMapping("/reissue")
    public ResponseEntity<Object> reissue(@RequestHeader("Authorization") String accessToken) {
        log.info("Token Reissue API 호출");
        String newAccessToken = tokenService.reissueToken(accessToken);

        GlobalSuccessResponseDto<Object> reissueSuccessResponse =
                globalService.successResponse(REISSUE_TOKEN_SUCCESS.getMessage()
                        , new AccessTokenResponse(newAccessToken));
        return ResponseEntity.status(OK).body(reissueSuccessResponse);
    }
}
