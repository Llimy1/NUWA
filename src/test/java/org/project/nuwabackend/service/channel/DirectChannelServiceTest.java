package org.project.nuwabackend.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequest;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Direct Channel Service Test")
@ExtendWith(MockitoExtension.class)
class DirectChannelServiceTest {

    @Mock
    DirectChannelRepository directChannelRepository;
    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;

    @InjectMocks
    DirectChannelService directChannelService;

    private DirectChannelRequest directChannelRequest;
    private WorkSpace workSpace;
    private WorkSpaceMember senderWorkSpaceMember;
    private WorkSpaceMember receiverWorkSpaceMember;

    private Member sender;
    private Member receiver;

    @BeforeEach
    void setup() {

        Long workSpaceId = 1L;
        Long joinMemberId = 1L;

        String workSpaceName = "workSpaceName";
        String workSpaceImage = "workSpaceImage";
        String workSpaceIntroduce = "workSpaceIntroduce";

        String senderWorkSpaceMemberName = "createMember";
        String senderWorkSpaceMemberImage = "senderImage";
        String senderWorkSpaceMemberJob = "senderJob";

        String receiverWorkSpaceMemberName = "joinMemberName";
        String receiverWorkSpaceMemberImage = "receiverImage";
        String receiverWorkSpaceMemberJob = "receiverJob";

        String senderEmail = "senderEmail";
        String senderPassword = "senderPassword";
        String senderNickname = "senderNickname";
        String senderPhoneNumber = "senderPhoneNumber";

        String receiverEmail = "receiverEmail";
        String receiverPassword = "receiverPassword";
        String receiverNickname = "receiverNickname";
        String receiverPhoneNumber = "receiverPhoneNumber";

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);
        sender = Member.createMember(senderEmail, senderPassword, senderNickname, senderPhoneNumber);
        receiver = Member.createMember(receiverEmail, receiverPassword, receiverNickname, receiverPhoneNumber);

        senderWorkSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                senderWorkSpaceMemberName,
                senderWorkSpaceMemberJob,
                senderWorkSpaceMemberImage,
                WorkSpaceMemberType.CREATED,
                sender,
                workSpace);
        ReflectionTestUtils.setField(senderWorkSpaceMember, "id", 1L);

        receiverWorkSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                receiverWorkSpaceMemberName,
                receiverWorkSpaceMemberJob,
                receiverWorkSpaceMemberImage,
                WorkSpaceMemberType.JOIN,
                receiver,
                workSpace);
        ReflectionTestUtils.setField(receiverWorkSpaceMember, "id", 2L);

        directChannelRequest = new DirectChannelRequest(workSpaceId, joinMemberId);
    }

    @Test
    @DisplayName("[Service] Direct Channel Save Test")
    void saveDirectChannelTest() {
        //given
        Long joinMemberId = directChannelRequest.joinMemberId();
        Long workSpaceId = directChannelRequest.workSpaceId();

        Direct direct = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, receiverWorkSpaceMember);

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(senderWorkSpaceMember));
        given(workSpaceMemberRepository.findById(any()))
                .willReturn(Optional.of(receiverWorkSpaceMember));
        given(directChannelRepository.save(any()))
                .willReturn(direct);

        //when
        String directChannelId = directChannelService.createDirectChannel(sender.getEmail(), directChannelRequest);

        //then
        assertThat(directChannelId).isNotNull();
        verify(workSpaceMemberRepository).findByMemberEmailAndWorkSpaceId(sender.getEmail(), workSpaceId);
        verify(workSpaceMemberRepository).findById(joinMemberId);
    }
}