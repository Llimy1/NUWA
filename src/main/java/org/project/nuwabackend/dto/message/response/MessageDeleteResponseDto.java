package org.project.nuwabackend.dto.message.response;


import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

@Builder
public record MessageDeleteResponseDto(
        String id,
        Long workSpaceId,
        String roomId,
        String content,
        Boolean isDeleted,
        MessageType messageType) {
}
