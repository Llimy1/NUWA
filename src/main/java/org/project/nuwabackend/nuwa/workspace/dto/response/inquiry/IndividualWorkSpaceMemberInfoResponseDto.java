package org.project.nuwabackend.nuwa.workspace.dto.response.inquiry;

import lombok.Builder;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;

@Builder
public record IndividualWorkSpaceMemberInfoResponseDto(Long id,
                                                       String name,
                                                       String job,
                                                       String image,
                                                       String status,
                                                       String email,
                                                       String phoneNumber,
                                                       WorkSpaceMemberType workSpaceMemberType,
                                                       Boolean isDelete) {

}
