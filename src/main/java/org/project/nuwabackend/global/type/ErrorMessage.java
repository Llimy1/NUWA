package org.project.nuwabackend.global.type;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorMessage {

    DUPLICATE_NICKNAME("닉네임 중복입니다."),
    DUPLICATE_EMAIL("이메일 중복입니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
