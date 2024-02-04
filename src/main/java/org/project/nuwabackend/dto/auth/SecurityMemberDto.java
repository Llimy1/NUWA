package org.project.nuwabackend.dto.auth;

import lombok.Builder;
import org.project.nuwabackend.type.Role;


@Builder
public record SecurityMemberDto(Long id, String nickname, String email, String phoneNumber, Role role) {
}
