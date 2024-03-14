package org.project.nuwabackend.dto.message.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.project.nuwabackend.type.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChatMessageListResponseDto(
        String messageId,
        Long workSpaceId,
        String roomId,
        Long senderId,
        String senderName,
        String content,
        List<String> rawString,
        Long readCount,
        Boolean isEdited,
        Boolean isDeleted,
        MessageType messageType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime createdAt) {
}
