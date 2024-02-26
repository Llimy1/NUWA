package org.project.nuwabackend.dto.message.request;

import lombok.Builder;

@Builder
public record ChatMessageRequestDto(Long workSpaceId, String roomId, String content) {
}
