package org.project.nuwabackend.domain.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.nuwa.domain.channel.Chat;
import org.project.nuwabackend.nuwa.domain.channel.ChatJoinMember;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Chat Join Member Test")
class ChatJoinMemberTest {

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

    String chatChannelName = "chatChannelName";

    Chat channel;
    Member sender;
    WorkSpaceMember workSpaceCreateMember;
    WorkSpace workSpace;


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

        // Chat Channel
        channel = Chat.createChatChannel(chatChannelName, workSpace, workSpaceCreateMember);
    }

    @Test
    @DisplayName("[Domain] Create Chat Join Member Test")
    void createChatJoinMemberTest() {
        //given
        ChatJoinMember chatJoinMember =
                ChatJoinMember.createChatJoinMember(workSpaceCreateMember, channel);

        //when
        //then
        assertThat(chatJoinMember.getJoinMember()).isEqualTo(workSpaceCreateMember);
        assertThat(chatJoinMember.getChatChannel()).isEqualTo(channel);
    }

}