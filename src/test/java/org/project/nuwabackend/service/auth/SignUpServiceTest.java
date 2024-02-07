package org.project.nuwabackend.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.dto.auth.GeneratedTokenDto;
import org.project.nuwabackend.dto.auth.request.SingUpRequestDto;
import org.project.nuwabackend.dto.auth.request.SocialSignUpRequestDto;
import org.project.nuwabackend.global.exception.DuplicationException;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.ErrorMessage.DUPLICATE_EMAIL;
import static org.project.nuwabackend.global.type.ErrorMessage.DUPLICATE_NICKNAME;


@DisplayName("[Service] SignUp Service Test")
@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    SignUpService signUpService;

    private SingUpRequestDto singUpRequestDto;
    private SocialSignUpRequestDto socialSignUpRequestDto;

    @BeforeEach
    void setup() {
        String nickname = "nickname";
        String email = "email";
        String password = "password";
        String phoneNumber = "01000000000";
        String provider = "provider";

        singUpRequestDto = new SingUpRequestDto(nickname, email, password, phoneNumber);
        passwordEncoder = new BCryptPasswordEncoder();

        socialSignUpRequestDto = new SocialSignUpRequestDto(nickname, email, phoneNumber, provider);
    }

    @Test
    @DisplayName("[Service] SignUp Success")
    void signUpSuccess() {
        //given
        Member member = Member.createMember(
                singUpRequestDto.email(),
                singUpRequestDto.password(),
                singUpRequestDto.nickname(),
                singUpRequestDto.phoneNumber());

        member.passwordEncoder(passwordEncoder);

        given(memberRepository.save(any()))
                .willReturn(member);

        ReflectionTestUtils.setField(member, "id", 1L);

        //when
        Long memberId = signUpService.signUp(singUpRequestDto);

        //then
        assertThat(memberId).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("[Service] Social SignUp Success")
    void socialSignUpSuccess() {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        Member member = Member.createSocialMember(
                socialSignUpRequestDto.email(),
                socialSignUpRequestDto.nickname(),
                socialSignUpRequestDto.phoneNumber(),
                socialSignUpRequestDto.provider());

        GeneratedTokenDto generatedTokenDto =
                GeneratedTokenDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

        given(memberRepository.save(any()))
                .willReturn(member);
        given(jwtUtil.generatedToken(singUpRequestDto.email(), member.getRoleKey()))
                .willReturn(generatedTokenDto);


        //when
        GeneratedTokenDto tokenDto = signUpService.socialSignUp(socialSignUpRequestDto);

        //then
        assertThat(tokenDto).isEqualTo(generatedTokenDto);
    }

    @Test
    @DisplayName("[Service] Nickname Duplicate")
    void nicknameDuplicate() {
        //given
        given(memberRepository.findByNickname(anyString()))
                .willThrow(new DuplicationException(DUPLICATE_NICKNAME));

        //when
        //then
        assertThatThrownBy(() -> signUpService.duplicateNickname(singUpRequestDto.nickname()))
                .isInstanceOf(DuplicationException.class);
    }

    @Test
    @DisplayName("[Service] Email Duplicate")
    void emailDuplicate() {
        //given
        given(memberRepository.findByEmail(anyString()))
                .willThrow(new DuplicationException(DUPLICATE_EMAIL));

        //when
        //then
        assertThatThrownBy(() -> signUpService.duplicateEmail(singUpRequestDto.email()))
                .isInstanceOf(DuplicationException.class);
    }

}