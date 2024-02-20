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
                                       @Setter Long unReadCount,
                                       @Setter String lastMessage,
                                       @Setter LocalDateTime createdAt) {
}
