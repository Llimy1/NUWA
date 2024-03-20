package org.project.nuwabackend.nuwa.auth.dto.request;

public record SocialSignUpRequestDto(String nickname, String email, String phoneNumber, String provider) {
}
