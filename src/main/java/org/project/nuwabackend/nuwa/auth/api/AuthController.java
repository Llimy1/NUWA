package org.project.nuwabackend.nuwa.auth.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.auth.dto.request.LoginRequestDto;
import org.project.nuwabackend.nuwa.auth.dto.GeneratedTokenDto;
import org.project.nuwabackend.nuwa.auth.dto.response.AccessTokenResponse;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.auth.service.LoginService;
import org.project.nuwabackend.nuwa.auth.service.token.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.response.type.SuccessMessage.LOGIN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.LOGOUT_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.PASSWORD_CHANGE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.REISSUE_TOKEN_SUCCESS;
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
        tokenService.saveTokenInfo(email, accessToken, refreshToken);

        GlobalSuccessResponseDto<Object> loginSuccessResponse =
                globalService.successResponse(LOGIN_SUCCESS.getMessage(), new AccessTokenResponse(accessToken));

        return ResponseEntity.status(CREATED).body(loginSuccessResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@MemberEmail String email) {
        log.info("Logout API 호출");
        tokenService.removeRefreshToken(email);

        GlobalSuccessResponseDto<Object> logoutSuccessResponse =
                globalService.successResponse(LOGOUT_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(logoutSuccessResponse);
    }

    @PostMapping("/reissue")
    public ResponseEntity<Object> reissue(@RequestHeader(value = "Authorization") String accessToken,
                                          @RequestParam(value = "email") String email) {
        log.info("Token Reissue API 호출");
        String newAccessToken = tokenService.reissueToken(accessToken, email);

        GlobalSuccessResponseDto<Object> reissueSuccessResponse =
                globalService.successResponse(REISSUE_TOKEN_SUCCESS.getMessage()
                        , new AccessTokenResponse(newAccessToken));
        return ResponseEntity.status(OK).body(reissueSuccessResponse);
    }

    @PatchMapping("/change")
    public ResponseEntity<Object> passwordChange(@MemberEmail String email,
                                                 @RequestParam(value = "password") String password) {
        log.info("비밀번호 변경 API 호출");
        loginService.passwordChange(email, password);

        GlobalSuccessResponseDto<Object> passwordChangeSuccess =
                globalService.successResponse(PASSWORD_CHANGE_SUCCESS.getMessage()
                        , null);
        return ResponseEntity.status(OK).body(passwordChangeSuccess);
    }
}
