package org.project.nuwabackend.nuwa.channel.dto.response;

import lombok.Builder;

@Builder
public record DirectChannelListResponseDto(String roomId, String name, Long workSpaceId, Long createMemberId, Long joinMemberId, String createMemberName, String joinMemberName) {
}
