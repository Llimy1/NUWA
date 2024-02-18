package org.project.nuwabackend.dto.workspace.response;

import lombok.Builder;
import org.project.nuwabackend.type.WorkSpaceMemberType;

@Builder
public record WorkSpaceMemberInfoResponse( String name,
                                           String job,
                                           String image,
                                           WorkSpaceMemberType workSpaceMemberType,
                                           String email,
                                           String nickname) {

}
