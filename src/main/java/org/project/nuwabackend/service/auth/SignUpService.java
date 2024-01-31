package org.project.nuwabackend.service.auth;

import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.domain.Member;
import org.project.nuwabackend.dto.auth.request.SingUpRequestDto;
import org.project.nuwabackend.global.exception.Duplication;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(SingUpRequestDto singUpRequestDto) {

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

    // 닉네임 중복
    public void duplicateNickname(String nickname) {
        memberRepository.findByNickname(nickname).ifPresent(e -> {
            throw new Duplication(DUPLICATE_NICKNAME);
        });
    }

    // 이메일 중복
    public void duplicateEmail(String email) {
        memberRepository.findByEmail(email).ifPresent(e -> {
            throw new Duplication(DUPLICATE_EMAIL);
        });
    }
}
