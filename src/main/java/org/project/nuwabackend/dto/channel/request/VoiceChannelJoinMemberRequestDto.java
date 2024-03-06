package org.project.nuwabackend.dto.channel.request;

import java.util.List;

public record VoiceChannelJoinMemberRequestDto(Long voiceChannelId, List<Long> joinMemberIdList) {
}
