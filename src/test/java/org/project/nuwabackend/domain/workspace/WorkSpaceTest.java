package org.project.nuwabackend.domain.workspace;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("[Domain] Update WorkSpace Test")
    void updateWorkSpaceTest() {
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

    @Test
    @DisplayName("[Domain] Increase WorkSpace Member Count Test")
    void increaseWorkSpaceMemberCountTest() {
        //given
        WorkSpace workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        //when
        workSpace.increaseWorkSpaceMemberCount();

        //then
        assertThat(workSpace.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("[Domain] Decrease WorkSpace Member Count Test")
    void decreaseWorkSpaceMemberCountTest() {
        //given
        WorkSpace workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);
        workSpace.increaseWorkSpaceMemberCount();
        workSpace.increaseWorkSpaceMemberCount();

        //when
        workSpace.decreaseWorkSpaceMemberCount();

        //then
        assertThat(workSpace.getCount()).isEqualTo(1);
    }
}