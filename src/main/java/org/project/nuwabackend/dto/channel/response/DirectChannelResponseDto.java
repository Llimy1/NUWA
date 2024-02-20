package org.project.nuwabackend.dto.channel.response;

import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
public record DirectChannelResponseDto(String roomId,
                                       String name,
                                       Long workSpaceId,
                                       String createMemberName,
                                       String joinMemberName,
                                       Long unReadCount,
                                       String lastMessage,
                                       LocalDateTime createdAt) {
}
