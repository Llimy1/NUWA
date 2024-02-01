package org.project.nuwabackend.domain.member;

import lombok.Builder;
import lombok.Getter;
import org.project.nuwabackend.global.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.project.nuwabackend.global.type.ErrorMessage.OAUTH_PROVIDER_NOT_FOUND;

@Getter
public class OAuth2Attribute {

    private final Map<String, Object> attributes; // 사용자 속성 정보를 담는 Map
    private final String attributeKey; // 사용자 속성의 키 값
    private final String email; // 이메일 정보
    private final String provider; // 제공자 정보

    @Builder
    private OAuth2Attribute(Map<String, Object> attributes, String attributeKey, String email, String provider) {
        this.attributes = attributes;
        this.attributeKey = attributeKey;
        this.email = email;
        this.provider = provider;
    }

    // 서비스에 따라 OAuth2Attribute 객체를 생성하는 메서드
    public static OAuth2Attribute of(String provider, String attributeKey,
                              Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> ofGoogle(provider, attributeKey, attributes);
            case "kakao" -> ofKakao(provider, "email", attributes);
            case "naver" -> ofNaver(provider, "id", attributes);
            default -> throw new NotFoundException(OAUTH_PROVIDER_NOT_FOUND);
        };
    }

    /*
     *   Google 로그인일 경우 사용하는 메서드, 사용자 정보가 따로 Wrapping 되지 않고 제공되어,
     *   바로 get() 메서드로 접근이 가능하다.
     * */
    private static OAuth2Attribute ofGoogle(String provider, String attributeKey, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .email((String) attributes.get("email"))
                .provider(provider)
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    /*
     *  Naver 로그인일 경우 사용하는 메서드, 필요한 사용자 정보가 response Map에 감싸져 있어서,
     *  한번 get() 메서드를 이용해 사용자 정보를 담고있는 Map을 꺼내야한다.
     * */
    private static OAuth2Attribute ofNaver(String provider, String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                .email((String) response.get("email"))
                .provider(provider)
                .attributes(response)
                .attributeKey(attributeKey)
                .build();
    }

    /*
     *   Kakao 로그인일 경우 사용하는 메서드, 필요한 사용자 정보가 kakaoAccount -> kakaoProfile 두번 감싸져 있어서,
     *   두번 get() 메서드를 이용해 사용자 정보를 담고있는 Map을 꺼내야한다.
     * */
    private static OAuth2Attribute ofKakao(String provider, String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuth2Attribute.builder()
                .email((String) kakaoAccount.get("email"))
                .provider(provider)
                .attributes(kakaoAccount)
                .attributeKey(attributeKey)
                .build();
    }

    // OAuth2User 객체에 넣어주기 위해서 Map으로 값들을 반환해준다.
    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("email", email);
        map.put("provider", provider);

        return map;
    }
}
