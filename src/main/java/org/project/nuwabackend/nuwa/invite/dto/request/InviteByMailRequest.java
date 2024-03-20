package org.project.nuwabackend.nuwa.invite.dto.request;

import java.util.List;

public record InviteByMailRequest(Long workSpaceId, List<String> emailAddress) {
}
