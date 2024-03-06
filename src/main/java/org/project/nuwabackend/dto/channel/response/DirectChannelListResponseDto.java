package org.project.nuwabackend.dto.channel.response;

import lombok.Builder;

@Builder
public record DirectChannelListResponseDto(String roomId, String name, Long workSpaceId, Long createMemberId, Long joinMemberId, String createMemberName, String joinMemberName) {
}
