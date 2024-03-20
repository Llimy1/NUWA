package org.project.nuwabackend.nuwa.auth.dto.request;

public record SingUpRequestDto(String nickname, String email, String password, String phoneNumber) {
}
