package org.project.nuwabackend.dto.message.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DirectMessageRequestDto(String roomId, Long senderId, String senderName, String content) {

}
