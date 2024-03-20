package org.project.nuwabackend.nuwa.websocket.dto.request;

import lombok.Builder;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;

import java.util.List;

@Builder
public record ChatMessageRequestDto(Long workSpaceId, String roomId, String content, List<String> rawString, MessageType messageType) {
}
