package org.project.nuwabackend.global.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record GlobalErrorResponseDto(String status, String message) {
}
