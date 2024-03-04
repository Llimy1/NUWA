package org.project.nuwabackend.dto.message.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DirectMessageRequestDto(Long workSpaceId, String roomId, Long receiverId, String content) {

}
