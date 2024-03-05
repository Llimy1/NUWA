package org.project.nuwabackend.dto.message.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

import java.time.LocalDateTime;

@Builder
public record DirectMessageResponseDto(
        Long workSpaceId,
        String roomId,
        Long senderId,
        String senderName,
        String content,
        Long readCount,
        MessageType messageType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime createdAt) {
}
