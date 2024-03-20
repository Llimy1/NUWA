package org.project.nuwabackend.nuwa.websocket.dto.response;


import lombok.Builder;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;

@Builder
public record MessageDeleteResponseDto(
        String id,
        Long workSpaceId,
        String roomId,
        String content,
        Boolean isDeleted,
        MessageType messageType) {
}
