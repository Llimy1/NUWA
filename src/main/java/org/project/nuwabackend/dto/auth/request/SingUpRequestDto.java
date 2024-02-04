package org.project.nuwabackend.dto.auth.request;

public record SingUpRequestDto(String nickname, String email, String password, String phoneNumber) {
}
