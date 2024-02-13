package org.project.nuwabackend.dto.channel.request;

import java.util.List;

public record ChatChannelRequest(Long workSpaceId, List<String> joinMemberNameList) {
}
