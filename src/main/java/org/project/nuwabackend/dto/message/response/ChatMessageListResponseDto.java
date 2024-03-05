package org.project.nuwabackend.dto.message.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChatMessageListResponseDto(
        Long workSpaceId,
        String roomId,
        Long senderId,
        String senderName,
        String content,
        MessageType messageType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime createdAt) {
}
