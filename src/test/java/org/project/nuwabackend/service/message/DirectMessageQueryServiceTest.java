package org.project.nuwabackend.service.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;
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

        String senderEmail = "senderEmail";
        String senderPassword = "senderPassword";
        String senderNickname = "senderNickname";
        String senderPhoneNumber = "senderPhoneNumber";

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);
        sender = Member.createMember(senderEmail, senderPassword, senderNickname, senderPhoneNumber);
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
        given(workSpaceMemberRepository.findByMemberEmail(anyString()))
                .willReturn(Optional.of(senderWorkSpaceMember));
        //when
        directMessageQueryService.updateReadCountZero(directChannelRoomId, email);

        //then
        verify(mongoTemplate).updateMulti(any(Query.class), any(), eq(DirectMessage.class));
    }

    @Test
    @DisplayName("[Service] Count UnRead Message Test")
    void countUnReadMessageTest() {
        //given
        Long testCount = 10L;

        given(workSpaceMemberRepository.findByMemberEmail(anyString()))
                .willReturn(Optional.of(senderWorkSpaceMember));
        given(mongoTemplate.count(any(Query.class), eq(DirectMessage.class)))
                .willReturn(testCount);

        //when
        Long count = directMessageQueryService.countUnReadMessage(directChannelRoomId, email);

        //then
        verify(mongoTemplate).count(any(Query.class), eq(DirectMessage.class));
        assertThat(count).isEqualTo(testCount);

    }
}