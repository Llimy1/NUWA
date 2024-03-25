package org.project.nuwabackend.nuwa.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.auth.repository.redis.RefreshTokenRepository;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.auth.service.token.TokenService;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.auth.dto.request.LoginRequestDto;
import org.project.nuwabackend.nuwa.auth.dto.GeneratedTokenDto;
import org.project.nuwabackend.global.exception.custom.LoginException;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.response.type.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public GeneratedTokenDto login(LoginRequestDto loginRequestDto) {
        log.info("Login Service 호출");
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        refreshTokenRepository.findByEmail(email).ifPresent(e -> {
            throw new LoginException(DUPLICATE_LOGIN_BY_WEB);
        });

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

    @Transactional
    public void passwordChange(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MEMBER_ID_NOT_FOUND));

        member.passwordChange(password);
        member.passwordEncoder(passwordEncoder);
    }
}
