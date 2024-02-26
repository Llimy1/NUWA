package org.project.nuwabackend.dto.workspace.response;

import lombok.Builder;
import org.project.nuwabackend.type.WorkSpaceMemberType;

@Builder
public record IndividualWorkSpaceMemberInfoResponse(String name,
                                                    String job,
                                                    String image,
                                                    String email,
                                                    String phoneNumber,
                                                    WorkSpaceMemberType workSpaceMemberType) {

}
