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
import org.project.nuwabackend.domain.redis.DirectChannelRedis;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequest;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.repository.redis.DirectChannelRedisRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Direct Channel Service Test")
@ExtendWith(MockitoExtension.class)
class DirectServiceTest {

    @Mock
    DirectChannelRepository directChannelRepository;
    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    WorkSpaceRepository workSpaceRepository;
    @Mock
    DirectChannelRedisRepository directChannelRedisRepository;

    @InjectMocks
    DirectChannelService directChannelService;

    private DirectChannelRequest directChannelRequest;
    private WorkSpace workSpace;
    private WorkSpaceMember senderWorkSpaceMember;
    private WorkSpaceMember receiverWorkSpaceMember;

    private Member sender;
    private Member receiver;
    private DirectChannelRedis directChannelRedisOne;
    private DirectChannelRedis directChannelRedisTwo;
    private List<DirectChannelRedis> directChannelRedisList = new ArrayList<>();

    @BeforeEach
    void setup() {

        Long workSpaceId = 1L;
        String joinMemberName = "joinMemberName";

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

        String directRedisRoomId = "directRoomId";
        String emailRedis = "redisEmail";

        String emailRedisTwo = "redisEmailTwo";



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

        receiverWorkSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                receiverWorkSpaceMemberName,
                receiverWorkSpaceMemberJob,
                receiverWorkSpaceMemberImage,
                WorkSpaceMemberType.JOIN,
                receiver,
                workSpace);

        directChannelRedisOne = DirectChannelRedis.createDirectChannelRedis(
                directRedisRoomId,
                emailRedis
        );

        directChannelRedisTwo = DirectChannelRedis.createDirectChannelRedis(
                directRedisRoomId,
                emailRedisTwo
        );

        directChannelRedisList.add(directChannelRedisOne);
        directChannelRedisList.add(directChannelRedisTwo);


        directChannelRequest = new DirectChannelRequest(workSpaceId, joinMemberName);
    }

    @Test
    @DisplayName("[Service] Direct Channel Save Test")
    void saveDirectChannelTest() {
        //given
        String directReceiver = directChannelRequest.joinMemberName();
        Long workSpaceId = directChannelRequest.workSpaceId();;

        Direct direct = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, receiverWorkSpaceMember);

        given(workSpaceRepository.findById(any()))
                .willReturn(Optional.of(workSpace));
        given(workSpaceMemberRepository.findByMemberEmail(anyString()))
                .willReturn(Optional.of(senderWorkSpaceMember));
        given(workSpaceMemberRepository.findByName(anyString()))
                .willReturn(Optional.of(receiverWorkSpaceMember));
        given(directChannelRepository.save(any()))
                .willReturn(direct);

        //when
        String directChannelId = directChannelService.createDirectChannel(sender.getEmail(), directChannelRequest);

        //then
        assertThat(directChannelId).isNotNull();
        verify(workSpaceRepository).findById(workSpaceId);
        verify(workSpaceMemberRepository).findByMemberEmail(sender.getEmail());
        verify(workSpaceMemberRepository).findByName(directReceiver);
    }

    @Test
    @DisplayName("[Service] Save Direct Channel Info Redis")
    void saveDirectChannelInfoRedis() {
        //given
        given(directChannelRedisRepository.save(any()))
                .willReturn(directChannelRedisOne);

        //when
        directChannelService.saveDirectChannelMemberInfo(
                directChannelRedisOne.getDirectRoomId(),
                directChannelRedisOne.getEmail());

        //then
        verify(directChannelRedisRepository).save(directChannelRedisOne);
    }

    @Test
    @DisplayName("[Service] Delete Direct Channel Info Redis")
    void deleteDirectChannelInfoRedis() {
        //given
        given(directChannelRedisRepository.findByDirectRoomIdAndEmail(anyString(), anyString()))
                .willReturn(Optional.of(directChannelRedisOne));

        //when
        directChannelService.deleteDirectChannelMemberInfo(
                directChannelRedisOne.getDirectRoomId(),
                directChannelRedisOne.getEmail());

        //then
        verify(directChannelRedisRepository).findByDirectRoomIdAndEmail(
                directChannelRedisOne.getDirectRoomId(),
                directChannelRedisOne.getEmail());
    }

    @Test
    @DisplayName("[Service] Is All Connected Test")
    void isAllConnectedTest() {
        //given
        given(directChannelRedisRepository.findByDirectRoomId(anyString()))
                .willReturn(directChannelRedisList);

        //when
        boolean allConnected = directChannelService.isAllConnected(directChannelRedisOne.getDirectRoomId());

        //then
        assertThat(allConnected).isTrue();
    }
}