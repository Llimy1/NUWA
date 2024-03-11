package org.project.nuwabackend.dto.message.request;


import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

@Builder
public record MessageDeleteRequestDto(
        String id,
        Long workSpaceId,
        String roomId,
        MessageType messageType) {
}
