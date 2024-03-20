package org.project.nuwabackend.nuwa.websocket.dto.request;


import lombok.Builder;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;

@Builder
public record MessageUpdateRequestDto(
        String id,
        Long workSpaceId,
        String roomId,
        String content,
        MessageType messageType) {
}
