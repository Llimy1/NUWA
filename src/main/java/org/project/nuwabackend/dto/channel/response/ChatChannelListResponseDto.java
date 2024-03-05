package org.project.nuwabackend.dto.channel.response;

import lombok.Builder;

@Builder
public record ChatChannelListResponseDto(Long workSpaceId,
                                         Long channelId,
                                         String roomId,
                                         String name) {
}
