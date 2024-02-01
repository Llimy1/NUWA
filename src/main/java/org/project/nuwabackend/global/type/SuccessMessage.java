package org.project.nuwabackend.global.type;

import lombok.Getter;

@Getter
public enum SuccessMessage {
    SIGNUP_SUCCESS("회원 가입에 성공 했습니다."),
    LOGIN_SUCCESS("로그인에 성공 했습니다."),
    SOCIAL_LOGIN_SUCCESS("소셜 로그인에 성공 했습니다."),
    LOGOUT_SUCCESS("로그아웃에 성공 했습니다."),
    NICKNAME_USE_OK("사용 가능한 닉네임 입니다."),
    EMAIL_USE_OK("사용 가능한 이메일 입니다."),
    REISSUE_TOKEN_SUCCESS("토큰 재발급에 성공 했습니다.");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
