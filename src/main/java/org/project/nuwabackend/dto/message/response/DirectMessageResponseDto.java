package org.project.nuwabackend.dto.message.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DirectMessageResponseDto(String roomId, String sender, String content, Integer readCount, LocalDateTime createdAt) {
}
