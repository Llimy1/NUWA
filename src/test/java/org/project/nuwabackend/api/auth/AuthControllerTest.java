package org.project.nuwabackend.api.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.auth.request.LoginRequestDto;
import org.project.nuwabackend.dto.auth.GeneratedTokenDto;
import org.project.nuwabackend.dto.auth.response.AccessTokenResponse;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.auth.LoginService;
import org.project.nuwabackend.service.auth.TokenService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.LOGIN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.LOGOUT_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.REISSUE_TOKEN_SUCCESS;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[API] Auth Controller Test")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    LoginService loginService;

    @Mock
    GlobalService globalService;

    @Mock
    TokenService tokenService;

    @InjectMocks
    AuthController authController;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    private LoginRequestDto loginRequestDto() {
        String email = "email";
        String password = "password";

        return new LoginRequestDto(email, password);
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("[API] Login Success")
    void loginSuccess() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(loginRequestDto());

        GeneratedTokenDto tokenDto =
                GeneratedTokenDto.builder()
                        .accessToken(ACCESS_TOKEN)
                        .refreshToken(REFRESH_TOKEN)
                        .build();
        AccessTokenResponse accessTokenResponse =
                new AccessTokenResponse(tokenDto.accessToken());

        GlobalSuccessResponseDto<Object> loginSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(LOGIN_SUCCESS.getMessage())
                        .data(accessTokenResponse)
                        .build();

        given(loginService.login(any())).willReturn(tokenDto);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(loginSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/login")
                .contentType(APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(LOGIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.accessToken")
                        .value(tokenDto.accessToken()))
                .andDo(print());

    }

    @Test
    @DisplayName("[API] Logout Success")
    void logoutSuccess() throws Exception {
        //given
        GlobalSuccessResponseDto<Object> logoutSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(LOGOUT_SUCCESS.getMessage())
                        .data(null)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(logoutSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/logout")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(LOGOUT_SUCCESS.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Reissue Success")
    void reissueSuccess() throws Exception {
        //given

        String newAccessToken = "new" + ACCESS_TOKEN;
        AccessTokenResponse accessTokenResponse =
                new AccessTokenResponse(newAccessToken);

        GlobalSuccessResponseDto<Object> reissueSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(REISSUE_TOKEN_SUCCESS.getMessage())
                        .data(accessTokenResponse)
                        .build();

        given(tokenService.reissueToken(anyString()))
                .willReturn(newAccessToken);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(reissueSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/reissue")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(REISSUE_TOKEN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.accessToken")
                        .value(accessTokenResponse.accessToken()))
                .andDo(print());

    }
}