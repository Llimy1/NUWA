package org.project.nuwabackend.nuwa.workspace.dto.response.inquiry;

import lombok.Builder;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;

@Builder
public record FavoriteWorkSpaceMemberInfoResponseDto(Long id,
                                                     String name,
                                                     String job,
                                                     String image,
                                                     String email,
                                                     String phoneNumber,
                                                     Long messageCount,
                                                     WorkSpaceMemberType workSpaceMemberType) {

}
