package org.project.nuwabackend.nuwa.websocket.dto.request;


import lombok.Builder;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;

@Builder
public record MessageDeleteRequestDto(
        String id,
        Long workSpaceId,
        String roomId,
        MessageType messageType) {
}
