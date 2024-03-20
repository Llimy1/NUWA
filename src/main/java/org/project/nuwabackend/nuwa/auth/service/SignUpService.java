package org.project.nuwabackend.nuwa.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.auth.dto.GeneratedTokenDto;
import org.project.nuwabackend.nuwa.auth.dto.request.SingUpRequestDto;
import org.project.nuwabackend.nuwa.auth.dto.request.SocialSignUpRequestDto;
import org.project.nuwabackend.global.exception.custom.DuplicationException;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.response.type.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignUpService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Long signUp(SingUpRequestDto singUpRequestDto) {
        log.info("Signup Service 호출");
        String email = singUpRequestDto.email();
        String password = singUpRequestDto.password();
        String nickname = singUpRequestDto.nickname();
        String phoneNumber = singUpRequestDto.phoneNumber();

        // 중복이면 오류 반환
        duplicateEmail(email);
        duplicateNickname(nickname);

        // 멤버 생성
        Member member = Member.createMember(email, password, nickname, phoneNumber);
        // 비밀번호 암호화
        member.passwordEncoder(passwordEncoder);

        // 멤버 저장
        Member saveMember = memberRepository.save(member);

        // 반환
        return saveMember.getId();
    }

    @Transactional
    public GeneratedTokenDto socialSignUp(SocialSignUpRequestDto socialSignUpRequestDto) {
        log.info("Social SignUp Service 호출");
        String email = socialSignUpRequestDto.email();
        String nickname = socialSignUpRequestDto.nickname();
        String phoneNumber = socialSignUpRequestDto.phoneNumber();
        String provider = socialSignUpRequestDto.provider();

        duplicateEmail(email);
        duplicateNickname(nickname);

        Member socialMember =
                Member.createSocialMember(email, nickname, phoneNumber, provider);

        Member saveSocialMember = memberRepository.save(socialMember);

        String role = saveSocialMember.getRoleKey();

        return jwtUtil.generatedToken(email, role);
    }



    // 닉네임 중복
    public void duplicateNickname(String nickname) {
        log.info("duplicateNickname 호출");
        memberRepository.findByNickname(nickname).ifPresent(e -> {
            throw new DuplicationException(DUPLICATE_NICKNAME);
        });
    }

    // 이메일 중복
    public void duplicateEmail(String email) {
        log.info("duplicateEmail 호출");
        memberRepository.findByEmail(email).ifPresent(e -> {
            throw new DuplicationException(DUPLICATE_EMAIL);
        });
    }
}
