package org.project.nuwabackend.dto.message;

import lombok.Builder;
import org.project.nuwabackend.domain.mongo.DirectMessage;

import java.time.LocalDateTime;

@Builder
public record DirectMessageDto(String roomId, Long senderId, String senderName, String content, Long readCount, LocalDateTime createdAt) {

}
