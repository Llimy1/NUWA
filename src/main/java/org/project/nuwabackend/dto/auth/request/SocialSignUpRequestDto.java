package org.project.nuwabackend.dto.auth.request;

public record SocialSignUpRequestDto(String nickname, String email, String phoneNumber, String provider) {
}
