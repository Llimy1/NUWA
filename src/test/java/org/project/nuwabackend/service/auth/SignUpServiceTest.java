package org.project.nuwabackend.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.Member;
import org.project.nuwabackend.dto.auth.request.SingUpRequestDto;
import org.project.nuwabackend.global.exception.Duplication;
import org.project.nuwabackend.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;


@DisplayName("[Service] SignUp Service Test")
@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    SignUpService signUpService;

    private SingUpRequestDto singUpRequestDto;

    @BeforeEach
    void init() {
        String nickname = "nickname";
        String email = "email";
        String password = "password";
        String phoneNumber = "01000000000";

        singUpRequestDto = new SingUpRequestDto(nickname, email, password, phoneNumber);
        passwordEncoder = new BCryptPasswordEncoder();
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
        System.out.println("memberId = " + memberId);

        //then
        assertThat(memberId).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("[Service] Nickname Duplicate")
    void nicknameDuplicate() {
        //given
        Member member = Member.createMember(
                singUpRequestDto.email(),
                singUpRequestDto.password(),
                singUpRequestDto.nickname(),
                singUpRequestDto.phoneNumber());

        member.passwordEncoder(passwordEncoder);

        given(memberRepository.findByNickname(anyString()))
                .willReturn(Optional.of(member));

        //when
        //then
        assertThatThrownBy(() -> signUpService.duplicateNickname(singUpRequestDto.nickname()))
                .isInstanceOf(Duplication.class);
    }

    @Test
    @DisplayName("[Service] Email Duplicate")
    void emailDuplicate() {
        //given
        Member member = Member.createMember(
                singUpRequestDto.email(),
                singUpRequestDto.password(),
                singUpRequestDto.nickname(),
                singUpRequestDto.phoneNumber());

        member.passwordEncoder(passwordEncoder);

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        //when
        //then
        assertThatThrownBy(() -> signUpService.duplicateEmail(singUpRequestDto.email()))
                .isInstanceOf(Duplication.class);
    }
}