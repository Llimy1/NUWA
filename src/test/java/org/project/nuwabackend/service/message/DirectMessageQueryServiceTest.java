package org.project.nuwabackend.service.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.domain.channel.Direct;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.mongo.DirectMessage;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.channel.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;
import org.project.nuwabackend.nuwa.websocket.service.DirectMessageQueryService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Direct Message Query Service Test")
@ExtendWith(MockitoExtension.class)
class DirectMessageQueryServiceTest {

    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    DirectChannelRepository directChannelRepository;
    @Mock
    MongoTemplate mongoTemplate;

    @InjectMocks
    DirectMessageQueryService directMessageQueryService;

    String directChannelRoomId = "directChannelRoomId";
    String email = "abcd@gmail.com";

    WorkSpaceMember senderWorkSpaceMember;
    Member sender;
    WorkSpace workSpace;


    @BeforeEach
    void setup() {

        String workSpaceName = "workSpaceName";
        String workSpaceImage = "workSpaceImage";
        String workSpaceIntroduce = "workSpaceIntroduce";

        String senderWorkSpaceMemberName = "createMember";
        String senderWorkSpaceMemberImage = "senderImage";
        String senderWorkSpaceMemberJob = "senderJob";

        String senderPassword = "senderPassword";
        String senderNickname = "senderNickname";
        String senderPhoneNumber = "senderPhoneNumber";

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);
        ReflectionTestUtils.setField(workSpace, "id", 1L);

        sender = Member.createMember(email, senderPassword, senderNickname, senderPhoneNumber);
        senderWorkSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                senderWorkSpaceMemberName,
                senderWorkSpaceMemberJob,
                senderWorkSpaceMemberImage,
                WorkSpaceMemberType.CREATED,
                sender,
                workSpace);
        ReflectionTestUtils.setField(senderWorkSpaceMember, "id", 1L);
    }

    @Test
    @DisplayName("[Service] Update Read Count Zero Test")
    void updateReadCountZeroTest() {
        //given
        Member member = Member.createMember("abcd@gmail.com", "1234", "abcd", "01000000000");
        WorkSpaceMember workSpaceMember2 =
                WorkSpaceMember.joinWorkSpaceMember("홍길동", "N", WorkSpaceMemberType.JOIN, member, workSpace);

        Direct directChannel = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, workSpaceMember2);
        String roomId = directChannel.getRoomId();

        given(directChannelRepository.findByRoomId(anyString()))
                .willReturn(Optional.of(directChannel));
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(senderWorkSpaceMember));
        //when
        directMessageQueryService.updateReadCountZero(roomId, email);

        //then
        verify(mongoTemplate).updateMulti(any(Query.class), any(), eq(DirectMessage.class));
    }

    @Test
    @DisplayName("[Service] Count UnRead Message Test")
    void countUnReadMessageTest() {
        //given
        Long testCount = 10L;
        Long workSpaceId = 1L;
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(senderWorkSpaceMember));
        given(mongoTemplate.count(any(Query.class), eq(DirectMessage.class)))
                .willReturn(testCount);

        //when
        Long count = directMessageQueryService.countUnReadMessage(directChannelRoomId, email, workSpaceId);

        //then
        verify(mongoTemplate).count(any(Query.class), eq(DirectMessage.class));
        assertThat(count).isEqualTo(testCount);

    }
}