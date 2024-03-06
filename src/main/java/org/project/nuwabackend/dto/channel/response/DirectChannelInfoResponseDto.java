package org.project.nuwabackend.dto.channel.response;

import lombok.Builder;

@Builder
public record DirectChannelInfoResponseDto(
        Long channelId,
        String channelName,
        Long createMemberId,
        String createMemberName,
        Long joinMemberId,
        String joinMemberName) {
}
