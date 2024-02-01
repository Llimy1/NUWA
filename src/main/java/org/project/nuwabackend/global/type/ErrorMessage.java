package org.project.nuwabackend.global.type;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorMessage {

    DUPLICATE_NICKNAME("닉네임 중복입니다."),
    DUPLICATE_EMAIL("이메일 중복입니다."),
    JWT_EXPIRED("토큰이 만료되었습니다."),
    JWT_NOT_NORMAL_TOKEN("정상적인 토큰이 아닙니다."),
    EMAIL_NOT_FOUND_MEMBER("존재하지 않는 아이디 입니다."),
    LOGIN_EMAIL_OR_PASSWORD_INACCURATE("아이디 또는 비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND("이메일로 리프레쉬 토큰 값을 찾을 수 없습니다."),
    OAUTH_PROVIDER_NOT_FOUND("소셜로그인 제공자를 찾을 수 없습니다."),
    OAUTH_ROLE_NOT_FOUND("권한 정보가 존재하지 않습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
