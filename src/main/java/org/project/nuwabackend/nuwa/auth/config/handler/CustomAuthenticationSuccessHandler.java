package org.project.nuwabackend.nuwa.auth.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.auth.dto.GeneratedTokenDto;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.auth.service.token.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.project.nuwabackend.global.response.type.ErrorMessage.OAUTH_ROLE_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Social Login Authentication Success");

        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 사용자 이메일을 가져온다
        String email = oAuth2User.getAttribute("email");
        // 서비스 제공 플랫폼(GOOGLE, NAVER)이 어디인지 가져온다.
        String provider = oAuth2User.getAttribute("provider");
        // CustomOAuth2UserService에서 셋팅한 로그인 회원 존재 여부를 가져온다.
        boolean isExist = Boolean.TRUE.equals(oAuth2User.getAttribute("exist"));
        boolean isBasic = Boolean.TRUE.equals(oAuth2User.getAttribute("basic"));
        boolean isGoogle = Boolean.TRUE.equals(oAuth2User.getAttribute("google"));
        boolean isKakao = Boolean.TRUE.equals(oAuth2User.getAttribute("kakao"));

        if (isGoogle) {
            throw new IllegalArgumentException("Google 계정으로 이미 가입되어 있습니다. Google 계정으로 로그인 해주세요.");
        } else if (isKakao) {
            throw new IllegalArgumentException("Kakao 계정으로 이미 가입되어 있습니다. Kakao 계정으로 로그인 해주세요.");
        }

        // OAuth2User로 부터 Role을 얻어온다.
        String role = oAuth2User.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalAccessError(OAUTH_ROLE_NOT_FOUND.getMessage())) // 존재하지 않으면 예외 반환
                .getAuthority();

        // 회원이 존재를 하면
        if (isExist || isBasic) {
            // jwt 토큰 발행
            GeneratedTokenDto tokenDto = jwtUtil.generatedToken(email, role);
            String accessToken = tokenDto.accessToken();
            String refreshToken = tokenDto.refreshToken();

            log.info("redis 토큰 저장");
            tokenService.saveTokenInfo(email, accessToken, refreshToken);

            log.info("redirect 준비");
            // accessToken을 쿼리스트링에 담는 url 생성
            String targetUrl = UriComponentsBuilder.fromUriString("https://nu-wa.online/loading/auth")
                    .queryParam("accessToken", accessToken)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            // 지정한 페이지로 리다이렉트 시킨다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            // 회원이 존재하지 않을경우, 서비스 제공자와 email을 쿼리스트링으로 전달하는 url을 만들어준다.
            String targetUrl = UriComponentsBuilder.fromUriString("https://nu-wa.online/signup/social")
                    .queryParam("email", email)
                    .queryParam("provider", provider)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            // 회원가입 페이지로 리다이렉트 시킨다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}