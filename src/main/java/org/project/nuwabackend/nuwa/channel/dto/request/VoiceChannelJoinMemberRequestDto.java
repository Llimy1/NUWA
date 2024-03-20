package org.project.nuwabackend.nuwa.channel.dto.request;

import java.util.List;

public record VoiceChannelJoinMemberRequestDto(Long voiceChannelId, List<Long> joinMemberIdList) {
}
