package org.project.nuwabackend.nuwa.auth.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.exception.custom.OAuth2Exception;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.project.nuwabackend.nuwa.auth.repository.redis.RefreshTokenRepository;
import org.project.nuwabackend.nuwa.auth.type.Role;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.member.OAuth2Attribute;
import org.project.nuwabackend.nuwa.domain.redis.RefreshToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        log.info("Social LoadUser 호출");
        // 기본 OAuth2UserService 객체 생성
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService =
                new DefaultOAuth2UserService();

        // OAuthUSerService를 사용하여 OAuth2User 정보 가져온다.
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // 클라이언트 등록 ID(google, naver, kakao)와 사용자 속성을 가져온다.
        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // OAuth2UserService를 사용하여 가져온 OAuth2User 정보로 OAuth2Attribute 객체를 만든다.
        OAuth2Attribute oAuth2Attribute =
                OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // OAuth2Attribute의 속성 값을 Map으로 변경
        Map<String, Object> userAttribute = oAuth2Attribute.convertToMap();

        // 사용자 email 정보를 가져온다
        String email = (String) userAttribute.get("email");

        if (email == null) {
            throw new OAuth2Exception("email 정보가 존재하지 않습니다.");
        }

        // provider 정보를 가져온다
        String provider = (String) userAttribute.get("provider");

//        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByEmail(email);
//
//        if (optionalToken.isEmpty()) {
//
//        }
//
//        if (optionalToken.isPresent()) {
//            RefreshToken refreshToken = optionalToken.get();
//            refreshTokenRepository.delete(refreshToken);
//        }

        Optional<Member> findMember = memberRepository.findByEmail(email);

        // 회원이 없는 경우
        if (findMember.isEmpty()) {
            userAttribute.put("exist", false);
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(Role.USER.getKey())),
                    userAttribute, "email");
        }

        Member member = findMember.get();
        String findProvider = member.getProvider();

        // 일반 회원가입이 되어 있는 경우 -> provider를 삽입해 소셜로그인도 가능하게 만든다.
        if (findProvider == null) {
            userAttribute.put("basic", true);
            member.updateProvider(provider);
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                    userAttribute, "email");
        }

        // 회원이 있는 경우
        if (!provider.equals(findMember.get().getProvider())) {
            String oAuth2Error;
            if (provider.equals("kakao")) {
                oAuth2Error = "Google 계정으로 이미 가입되어 있습니다. Google 계정으로 로그인 해주세요.";
            } else {
                oAuth2Error = "Kakao 계정으로 이미 가입되어 있습니다. Kakao 계정으로 로그인 해주세요.";
            }
            throw new OAuth2Exception(oAuth2Error);
        }

        userAttribute.put("exist", true);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                userAttribute, "email");
    }
}
