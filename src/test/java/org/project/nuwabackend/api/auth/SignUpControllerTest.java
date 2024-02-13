package org.project.nuwabackend.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.auth.GeneratedTokenDto;
import org.project.nuwabackend.dto.auth.request.SingUpRequestDto;
import org.project.nuwabackend.dto.auth.request.SocialSignUpRequestDto;
import org.project.nuwabackend.dto.auth.response.AccessTokenResponse;
import org.project.nuwabackend.dto.auth.response.MemberIdResponseDto;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.auth.SignUpService;
import org.project.nuwabackend.service.auth.TokenService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.*;
import static org.project.nuwabackend.global.type.SuccessMessage.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[API] SignUp Controller Test")
@ExtendWith(MockitoExtension.class)
class SignUpControllerTest {

    @Mock
    private SignUpService signUpService;

    @Mock
    private GlobalService globalService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SignUpController signUpController;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private SingUpRequestDto singUpRequestDto() {
        String nickname = "nickname";
        String email = "email";
        String password = "password";
        String phoneNumber = "01000000000";

        return new SingUpRequestDto(nickname, email, password, phoneNumber);
    }

    private SocialSignUpRequestDto socialSignUpRequestDto(){
        String nickname = "nickname";
        String email = "email";
        String phoneNumber = "01000000000";
        String provider = "provider";
        return new SocialSignUpRequestDto(nickname, email, provider, phoneNumber);
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(signUpController).build();
    }


    @Test
    @DisplayName("[API] SignUp Success")
    void signUpSuccess() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(singUpRequestDto());
        Long memberId = 1L;
        MemberIdResponseDto memberIdResponseDto = new MemberIdResponseDto(memberId);

        GlobalSuccessResponseDto<Object> signUpSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(SIGNUP_SUCCESS.getMessage())
                        .data(memberIdResponseDto)
                        .build();

        given(signUpService.signUp(any()))
                .willReturn(memberId);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(signUpSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(SIGNUP_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.memberId")
                        .value(memberId))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Social SignUp Success")
    void socialSignUpSuccess() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        String body = objectMapper.writeValueAsString(socialSignUpRequestDto());

        GeneratedTokenDto generatedTokenDto =
                GeneratedTokenDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

        AccessTokenResponse accessTokenResponse =
                new AccessTokenResponse(generatedTokenDto.accessToken());

        GlobalSuccessResponseDto<Object> signUpSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(SOCIAL_LOGIN_SUCCESS.getMessage())
                        .data(accessTokenResponse)
                        .build();

        given(signUpService.socialSignUp(any()))
                .willReturn(generatedTokenDto);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(signUpSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/signup/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(SOCIAL_LOGIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.accessToken")
                        .value(accessToken))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Nickname Use Success")
    void nicknameUse() throws Exception {
        //given
        GlobalSuccessResponseDto<Object> nicknameUseResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(NICKNAME_USE_OK.getMessage())
                        .data(null)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(nicknameUseResponse);

        //when
        //then
        mvc.perform(get("/api/check/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nickname", "nickname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(NICKNAME_USE_OK.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("[Controller] email Use Success")
    void emailUse() throws Exception {
        //given
        GlobalSuccessResponseDto<Object> emailUseResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(EMAIL_USE_OK.getMessage())
                        .data(null)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(emailUseResponse);

        //when
        //then
        mvc.perform(get("/api/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(EMAIL_USE_OK.getMessage()))
                .andDo(print());
    }
}