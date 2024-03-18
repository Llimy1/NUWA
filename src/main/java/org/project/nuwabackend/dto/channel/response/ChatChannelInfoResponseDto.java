package org.project.nuwabackend.dto.channel.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatChannelInfoResponseDto(
        Long channelId,
        String channelName,
        List<Long> memberList) {
}
