package org.project.nuwabackend.nuwa.channel.dto.request;

import java.util.List;

public record ChatChannelJoinMemberRequestDto(Long chatChannelId, List<Long> joinMemberIdList) {
}
