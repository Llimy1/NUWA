package org.project.nuwabackend.dto.message.response;


import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

@Builder
public record MessageUpdateResponseDto(
        String id,
        Long workSpaceId,
        String roomId,
        String content,
        Boolean isEdited,
        MessageType messageType) {
}
