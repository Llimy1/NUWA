package org.project.nuwabackend.dto.message.request;


import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

@Builder
public record MessageUpdateRequestDto(
        String id,
        Long workSpaceId,
        String roomId,
        String content,
        MessageType messageType) {
}
