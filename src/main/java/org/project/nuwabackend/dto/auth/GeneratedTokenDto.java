package org.project.nuwabackend.dto.auth;

import lombok.Builder;

@Builder
public record GeneratedTokenDto(String accessToken, String refreshToken) {
}
