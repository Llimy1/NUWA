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
    CHAT_MESSAGE_LIST_RETURN_SUCCESS("채팅 채널 메세지 조회에 성공 했습니다."),
    DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS("채널 입장 정보 삭제에 성공 했습니다."),
    DIRECT_CHANNEL_LIST_RETURN_SUCCESS("다이렉트 채널 조회에 성공 했습니다."),
    DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS("마지막 채팅 순 다이렉트 채널 조회에 성공 했습니다."),
    SEARCH_DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS("검색한 마지막 채팅 순 다이렉트 채널 조회에 성공 했습니다."),
    CREATE_CHAT_CHANNEL_SUCCESS("채팅 채널 생성에 성공 했습니다."),
    CREATE_VOICE_CHANNEL_SUCCESS("음성 채널 생성에 성공 했습니다."),
    JOIN_CHAT_CHANNEL_SUCCESS("채팅 채널 참가에 성공 했습니다."),
    JOIN_VOICE_CHANNEL_SUCCESS("음성 채널 참가에 성공 했습니다."),
    FILE_UPLOAD_SUCCESS("파일 업로드에 성공 했습니다."),
    FILE_URL_RETURN_SUCCESS("파일 URL 조회에 성공 했습니다."),
    FILE_INFO_RETURN_SUCCESS("파일 정보 조회에 성공 했습니다."),
    SEARCH_FILE_INFO_RETURN_SUCCESS("파일 검색에 성공 했습니다."),
    TOP_SEVEN_FILE_INFO_RETURN_SUCCESS("최근 생성된 7개 파일을 가져오는데 성공 했습니다."),
    CREATE_WORK_SPACE_SUCCESS("워크스페이스 생성에 성공 했습니다."),
    READ_MY_WORK_SPACE_SUCCESS("워크스페이스 조회 성공 했습니다."),
    JOIN_WORK_SPACE_SUCCESS("워크스페이스 참가에 성공 했습니다."),
    WORK_SPACE_USE_SUCCESS("사용 가능한 워크스페이스 이름입니다."),
    INDIVIDUAL_WORK_SPACE_MEMBER_INFO_SUCCESS("개인 별 프로필 조회에 성공 했습니다."),
    WORK_SPACE_INFO_UPDATE_SUCCESS("워크스페이스 정보 편집에 성공 했습니다."),
    WORK_SPACE_MEMBER_INFO_UPDATE_SUCCESS("워크스페이스 멤버 정보 편집에 성공 했습니다."),
    NOTIFICATION_LIST_RETURN_SUCCESS("알림 정보 조회에 성공 했습니다.");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
