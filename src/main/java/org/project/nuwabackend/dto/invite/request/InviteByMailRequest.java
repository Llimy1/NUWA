package org.project.nuwabackend.dto.invite.request;

import java.util.List;

public record InviteByMailRequest(Long workSpaceId, List<String> emailAddress) {
}
