package org.project.nuwabackend.domain.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Direct Channel Domain Test")
class DirectTest {

    WorkSpace workSpace;
    String workSpaceName = "workSpaceName";
    String workSpaceImage = "workSpaceImage";
    String workSpaceIntroduce = "workSpaceIntroduce";

    String workSpaceMemberName = "workSpaceMemberName";
    String workSpaceMemberImage = "workSpaceMemberImage";
    String workSpaceMemberJob = "workSpaceJob";

    String senderEmail = "senderEmail";
    String senderPassword = "senderPassword";
    String senderNickname = "senderNickname";
    String senderPhoneNumber = "senderPhoneNumber";

    String receiverEmail = "receiverEmail";
    String receiverPassword = "receiverPassword";
    String receiverNickname = "receiverNickname";
    String receiverPhoneNumber = "receiverPhoneNumber";

    Member sender;
    Member receiver;

    WorkSpaceMember workSpaceCreateMember;
    WorkSpaceMember workSpaceJoinMember;

    @BeforeEach
    void setup() {
        sender = Member.createMember(senderEmail, senderPassword, senderNickname, senderPhoneNumber);
        receiver = Member.createMember(receiverEmail, receiverPassword, receiverNickname, receiverPhoneNumber);

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        workSpaceCreateMember = WorkSpaceMember.builder()
                .name(workSpaceMemberName)
                .job(workSpaceMemberJob)
                .image(workSpaceMemberImage)
                .member(sender)
                .build();

        workSpaceJoinMember = WorkSpaceMember.builder()
                .name(workSpaceMemberName)
                .job(workSpaceMemberJob)
                .image(workSpaceMemberImage)
                .member(receiver)
                .build();
    }

    @Test
    @DisplayName("[Domain] Create Direct Channel Test")
    void createDirectChannelTest() {
        //given
        //when
        Direct direct = Direct.createDirectChannel(workSpace, workSpaceCreateMember, workSpaceJoinMember);

        //thenR
        assertThat(direct.getRoomId()).isNotNull();
        assertThat(direct.getWorkSpace()).isEqualTo(workSpace);
        assertThat(direct.getCreateMember()).isEqualTo(workSpaceCreateMember);
        assertThat(direct.getJoinMember()).isEqualTo(workSpaceJoinMember);
    }

}