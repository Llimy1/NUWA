package org.project.nuwabackend.dto.channel.response;

import lombok.Builder;

@Builder
public record ChatChannelInfoResponseDto(
        Long channelId,
        String channelName) {
}
