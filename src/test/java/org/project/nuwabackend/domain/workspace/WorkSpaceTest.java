package org.project.nuwabackend.domain.workspace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.type.WorkSpaceMemberType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Domain] WorkSpace Domain Test")
class WorkSpaceTest {

    String workSpaceName = "workSpaceName";
    String workSpaceImage = "workSpaceImage";
    String workSpaceIntroduce = "workSpaceIntroduce";


    @Test
    @DisplayName("[Domain] Create WorkSpace Test")
    void createWorkSpaceTest() {
        //given
        WorkSpace workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        //when
        //then
        assertThat(workSpace.getName()).isEqualTo(workSpaceName);
        assertThat(workSpace.getImage()).isEqualTo(workSpaceImage);
        assertThat(workSpace.getIntroduce()).isEqualTo(workSpaceIntroduce);
    }

    @Test
    @DisplayName("[Domain] Update WorkSpace")
    void updateWorkSpace() {
        //given
        String updateWorkSpaceName = "newWorkSpaceName";
        String updateWorkSpaceImage = "newWorkSpaceImage";

        WorkSpace workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        //when
        workSpace.updateWorkSpace(updateWorkSpaceName, updateWorkSpaceImage);

        //then
        assertThat(workSpace.getName()).isEqualTo(updateWorkSpaceName);
        assertThat(workSpace.getImage()).isEqualTo(updateWorkSpaceImage);
    }
}