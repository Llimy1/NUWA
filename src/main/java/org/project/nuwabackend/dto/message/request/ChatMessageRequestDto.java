package org.project.nuwabackend.dto.message.request;

import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

@Builder
public record ChatMessageRequestDto(Long workSpaceId, String roomId, String content, MessageType messageType) {
}
