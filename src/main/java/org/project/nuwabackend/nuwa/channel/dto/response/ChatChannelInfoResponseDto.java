package org.project.nuwabackend.nuwa.channel.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatChannelInfoResponseDto(
        Long channelId,
        String channelName,
        List<Long> memberList) {
}
