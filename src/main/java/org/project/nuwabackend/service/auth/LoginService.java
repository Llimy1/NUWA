package org.project.nuwabackend.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.dto.auth.request.LoginRequestDto;
import org.project.nuwabackend.dto.auth.GeneratedTokenDto;
import org.project.nuwabackend.global.exception.LoginException;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional(readOnly = true)
    public GeneratedTokenDto login(LoginRequestDto loginRequestDto) {
        log.info("Login Service 호출");
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(EMAIL_NOT_FOUND_ID.getMessage()));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException aie) {
            throw new LoginException(LOGIN_EMAIL_OR_PASSWORD_INACCURATE);
        }

        return jwtUtil.generatedToken(email, member.getRoleKey());
    }
}
