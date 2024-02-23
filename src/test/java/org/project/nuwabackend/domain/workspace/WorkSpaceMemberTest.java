package org.project.nuwabackend.domain.workspace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.type.WorkSpaceMemberType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] WorkSpace Member Domain Test")
class WorkSpaceMemberTest {

    String workSpaceName = "workSpaceName";
    String workSpaceImage = "workSpaceImage";
    String workSpaceIntroduce = "workSpaceIntroduce";

    String workSpaceMemberName = "workSpaceMemberName";
    String workSpaceMemberImage = "workSpaceMemberImage";
    String workSpaceMemberJob = "workSpaceJob";

    String memberEmail = "senderEmail";
    String memberPassword = "senderPassword";
    String memberNickname = "senderNickname";
    String memberPhoneNumber = "senderPhoneNumber";

    Member member;
    WorkSpace workSpace;

    @BeforeEach
    void setup() {
        member = Member.createMember(memberEmail, memberPassword, memberNickname, memberPhoneNumber);
        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);
    }

    @Test
    @DisplayName("[Domain] Create WorkSpace Member Test")
    void createWorkSpaceMember() {
        //given
        WorkSpaceMember workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.CREATED,
                member,
                workSpace);

        //when
        //then
        assertThat(workSpaceMember.getName()).isEqualTo(workSpaceMemberName);
        assertThat(workSpaceMember.getJob()).isEqualTo(workSpaceMemberJob);
        assertThat(workSpaceMember.getImage()).isEqualTo(workSpaceMemberImage);
        assertThat(workSpaceMember.getMember()).isEqualTo(member);
        assertThat(workSpaceMember.getWorkSpace()).isEqualTo(workSpace);
        assertThat(workSpaceMember.getWorkSpaceMemberType()).isEqualTo(WorkSpaceMemberType.CREATED);
    }

    @Test
    @DisplayName("[Domain] Join WorkSpace Member Test")
    void joinWorkSpaceMember() {
        //given
        WorkSpaceMember workSpaceMember = WorkSpaceMember.joinWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberImage,
                WorkSpaceMemberType.JOIN,
                member,
                workSpace);

        //when
        //then
        assertThat(workSpaceMember.getName()).isEqualTo(workSpaceMemberName);
        assertThat(workSpaceMember.getImage()).isEqualTo(workSpaceMemberImage);
        assertThat(workSpaceMember.getMember()).isEqualTo(member);
        assertThat(workSpaceMember.getWorkSpace()).isEqualTo(workSpace);
        assertThat(workSpaceMember.getWorkSpaceMemberType()).isEqualTo(WorkSpaceMemberType.JOIN);
    }
}