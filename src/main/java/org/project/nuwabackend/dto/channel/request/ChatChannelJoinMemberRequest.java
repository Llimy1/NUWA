package org.project.nuwabackend.dto.channel.request;

import java.util.List;

public record ChatChannelJoinMemberRequest(Long chatChannelId, List<String> joinMemberNameList) {
}
