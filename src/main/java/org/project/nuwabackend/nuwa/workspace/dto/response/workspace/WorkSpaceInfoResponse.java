package org.project.nuwabackend.nuwa.workspace.dto.response.workspace;

import lombok.Builder;

@Builder
public record WorkSpaceInfoResponse(Long workspaceId,
                                    String workSpaceName,
                                    String workSpaceImage,
                                    String workSpaceIntroduce,
                                    Integer workSpaceMemberCount) {



}
