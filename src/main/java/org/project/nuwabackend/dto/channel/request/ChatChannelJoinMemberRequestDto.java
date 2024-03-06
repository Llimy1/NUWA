package org.project.nuwabackend.dto.channel.request;

import java.util.List;

public record ChatChannelJoinMemberRequestDto(Long chatChannelId, List<Long> joinMemberIdList) {
}
