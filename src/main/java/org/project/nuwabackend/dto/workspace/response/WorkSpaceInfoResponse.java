package org.project.nuwabackend.dto.workspace.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import org.project.nuwabackend.domain.workspace.WorkSpace;

import java.util.List;

@Builder
public record WorkSpaceInfoResponse(Long workspaceId,
                                    String workSpaceName,
                                    String workSpaceImage,
                                    String workSpaceIntroduce,
                                    Integer workSpaceMemberCount) {



}
