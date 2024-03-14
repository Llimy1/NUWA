package org.project.nuwabackend.dto.message.request;

import lombok.Builder;
import org.project.nuwabackend.type.MessageType;

import java.util.List;

@Builder
public record ChatMessageRequestDto(Long workSpaceId, String roomId, String content, List<String> rawString, MessageType messageType) {
}
