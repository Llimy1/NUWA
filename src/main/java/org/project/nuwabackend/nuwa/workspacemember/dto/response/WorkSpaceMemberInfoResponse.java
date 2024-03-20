package org.project.nuwabackend.nuwa.workspacemember.dto.response;

import lombok.Builder;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;

@Builder
public record WorkSpaceMemberInfoResponse(Long id,
                                          String name,
                                          String job,
                                          String image,
                                          WorkSpaceMemberType workSpaceMemberType,
                                          String email,
                                          String nickname) {

}
