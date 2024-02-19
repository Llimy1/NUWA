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
    REISSUE_TOKEN_SUCCESS("토큰 재발급에 성공 했습니다."),
    DIRECT_CHANNEL_CREATE_SUCCESS("다이렉트 채널 생성에 성공 했습니다."),
    DIRECT_MESSAGE_LIST_RETURN_SUCCESS("다이렉트 메세지 조회에 성공 했습니다."),
    DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS("채널 입장 정보 삭제에 성공 했습니다."),
    DIRECT_CHANNEL_LIST_RETURN_SUCCESS("다이렉트 채널 조회에 성공 했습니다."),
    CREATE_CHAT_CHANNEL_SUCCESS("채팅 채널 생성에 성공 했습니다."),
    CREATE_VOICE_CHANNEL_SUCCESS("음성 채널 생성에 성공 했습니다."),
    JOIN_CHAT_CHANNEL_SUCCESS("채팅 채널 참가에 성공 했습니다."),
    JOIN_VOICE_CHANNEL_SUCCESS("음성 채널 참가에 성공 했습니다."),
    CREATE_WORK_SPACE_SUCCESS("워크스페이스 생성에 성공 했습니다."),
    READ_MY_WORK_SPACE_SUCCESS("워크스페이스 조회에 성공 했습니다."),
    READ_MY_WORK_SPACE_MEMBER_SUCCESS("워크스페이스 멤버 조회에 성공 했습니다."),
    JOIN_WORK_SPACE_SUCCESS("워크스페이스 참가에 성공 했습니다.");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
