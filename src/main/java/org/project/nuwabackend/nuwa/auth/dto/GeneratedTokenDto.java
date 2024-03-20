package org.project.nuwabackend.nuwa.auth.dto;

import lombok.Builder;

@Builder
public record GeneratedTokenDto(String accessToken, String refreshToken) {
}
