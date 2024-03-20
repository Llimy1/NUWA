package org.project.nuwabackend.nuwa.auth.dto;

import lombok.Builder;
import org.project.nuwabackend.nuwa.auth.type.Role;


@Builder
public record SecurityMemberDto(Long id, String nickname, String email, String phoneNumber, Role role) {
}
