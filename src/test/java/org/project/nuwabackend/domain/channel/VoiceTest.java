package org.project.nuwabackend.domain.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Voice Channel Domain Test")
class VoiceTest {

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

    String voiceChannelName = "voiceChannelName";

    Member sender;

    WorkSpaceMember workSpaceCreateMember;

    @BeforeEach
    void setup() {
        sender = Member.createMember(senderEmail, senderPassword, senderNickname, senderPhoneNumber);

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        workSpaceCreateMember = WorkSpaceMember.builder()
                .name(workSpaceMemberName)
                .job(workSpaceMemberJob)
                .image(workSpaceMemberImage)
                .member(sender)
                .build();
    }


    @Test
    @DisplayName("[Domain] Create Voice Channel Test")
    void createVoiceChannelTest() {
        //given
        Voice voiceChannel = Voice.createVoiceChannel(voiceChannelName, workSpace, workSpaceCreateMember);

        //when
        //then
        assertThat(voiceChannel.getName()).isEqualTo(voiceChannelName);
        assertThat(voiceChannel.getCreateMember()).isEqualTo(workSpaceCreateMember);
    }


}