package org.project.nuwabackend.dto;

import java.util.List;

public record InviteByMailRequest(Long workSpaceId, List<String> emailAddress) {
}
