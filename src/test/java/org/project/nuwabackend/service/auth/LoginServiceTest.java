package org.project.nuwabackend.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.auth.service.LoginService;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.auth.dto.request.LoginRequestDto;
import org.project.nuwabackend.nuwa.auth.dto.GeneratedTokenDto;
import org.project.nuwabackend.global.exception.custom.LoginException;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.project.nuwabackend.nuwa.auth.type.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.project.nuwabackend.global.response.type.ErrorMessage.*;

@DisplayName("[Service] Login Service Test")
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    LoginService loginService;

    private Member member;

    private LoginRequestDto loginRequestDto;

    private static final String ACCESS_TOKEN = "Bearer accessToken";
    private static final String REFRESH_TOKEN = "Bearer refreshToken";

    @BeforeEach
    void setup() {
        String nickname = "nickname";
        String email = "email";
        String password = "password";
        String phoneNumber = "01000000000";

        member = Member.createMember(email, password, nickname, phoneNumber);
        member.passwordEncoder(passwordEncoder);

        loginRequestDto = new LoginRequestDto(email, password);
    }

    @Test
    @DisplayName("[Service] Login Success")
    void loginSuccess() {
        //given
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(authenticationManager.authenticate(any()))
                .willReturn(new UsernamePasswordAuthenticationToken(email, password));
        given(jwtUtil.generatedToken(anyString(), anyString()))
                .willReturn(GeneratedTokenDto.builder()
                        .accessToken(ACCESS_TOKEN)
                        .refreshToken(REFRESH_TOKEN)
                        .build());

        //when
        GeneratedTokenDto tokenDto = loginService.login(loginRequestDto);

        //then
        assertThat(tokenDto).isNotNull();
        assertThat(tokenDto.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(tokenDto.refreshToken()).isEqualTo(REFRESH_TOKEN);
        verify(memberRepository).findByEmail(email);
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(email, password));
        verify(jwtUtil).generatedToken(email, Role.USER.getKey());
    }

    @Test
    @DisplayName("[Service] Login Authentication Fail")
    void loginAuthenticationFail() {
        //given
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(authenticationManager.authenticate(any()))
                .willThrow(new LoginException(LOGIN_EMAIL_OR_PASSWORD_INACCURATE));

        //when
        //then
        assertThatThrownBy(() -> loginService.login(loginRequestDto))
                .isInstanceOf(LoginException.class);
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(email, password));

    }

    @Test
    @DisplayName("[Service] Login UsernameNotFound Fail")
    void loginUsernameNotFoundFail() {
        //given
        given(memberRepository.findByEmail(anyString()))
                .willThrow(new UsernameNotFoundException(EMAIL_NOT_FOUND_ID.getMessage()));

        //when
        //then
        assertThatThrownBy(() -> loginService.login(loginRequestDto))
                .isInstanceOf(UsernameNotFoundException.class);
        verify(memberRepository).findByEmail(loginRequestDto.email());
    }
}