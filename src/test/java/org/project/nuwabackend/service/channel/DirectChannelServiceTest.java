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
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequest;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponse;
import org.project.nuwabackend.dto.channel.response.DirectChannelResponseDto;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.mongo.DirectMessageRepository;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @Mock
    DirectMessageRepository directMessageRepository;
    @Mock
    DirectMessageQueryService directMessageQueryService;

    @InjectMocks
    DirectChannelService directChannelService;

    private DirectChannelRequest directChannelRequest;
    private WorkSpace workSpace;
    private WorkSpaceMember senderWorkSpaceMember;
    private WorkSpaceMember receiverWorkSpaceMember;

    private Member sender;
    private Member receiver;

    private PageRequest pageRequest;

    Long workSpaceId = 1L;
    String email = "abcd@gmail.com";

    @BeforeEach
    void setup() {


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

        pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

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

    @Test
    @DisplayName("[Service] Direct Channel Slice Sort By CreatedDate")
    void directChannelSliceSortByCreatedDate() {
        //given
        Direct direct1 = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, receiverWorkSpaceMember);
        Direct direct2 = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, receiverWorkSpaceMember);
        Direct direct3 = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, receiverWorkSpaceMember);

        ReflectionTestUtils.setField(direct1, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(direct2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(direct3, "createdAt", LocalDateTime.now());

        List<Direct> directList = new ArrayList<>(List.of(direct1, direct2, direct3))
                .stream()
                .sorted(Comparator.comparing(Direct::getCreatedAt).reversed())
                .toList();

        Slice<Direct> directSlice = new SliceImpl<>(directList, pageRequest, false);

        Slice<DirectChannelListResponse> directSliceMap = directSlice.map(
                direct -> DirectChannelListResponse.builder()
                        .roomId(direct.getRoomId())
                        .name(direct.getName())
                        .createMemberId(direct.getCreateMember().getId())
                        .joinMemberId(direct.getJoinMember().getId())
                        .createMemberName(direct.getCreateMember().getName())
                        .joinMemberName(direct.getJoinMember().getName())
                        .build());

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(senderWorkSpaceMember));

        given(directChannelRepository.findDirectChannelByCreateMemberIdOrJoinMemberId(any(), any()))
                .willReturn(directSlice);
        //when
        Slice<DirectChannelListResponse> directChannelListResponseList =
                directChannelService.directChannelSlice(email, workSpaceId, pageRequest);
        //then
        assertThat(directChannelListResponseList).isNotNull();
        assertThat(directChannelListResponseList).containsAll(directSliceMap);
    }

    @Test
    @DisplayName("[Service] Direct Channel Slice Sort By Message CreateDate")
    void directChannelSliceSortByMessageCreateDate() {
        //given
        String roomId1 = "roomId1";
        String content1 = "content1";
        Long readCount = 0L;

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(senderWorkSpaceMember));

        DirectMessage directMessage1 = DirectMessage.createDirectMessage(workSpaceId, roomId1, sender.getId(), sender.getNickname(), content1, readCount, LocalDateTime.now());

        ReflectionTestUtils.setField(directMessage1, "id", UUID.randomUUID().toString());
        ReflectionTestUtils.setField(directMessage1, "createdAt", LocalDateTime.now());

        List<DirectChannelResponseDto> directChannelResponseDtoList = new ArrayList<>();

        Direct direct1 = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, receiverWorkSpaceMember);

        ReflectionTestUtils.setField(direct1, "createdAt", LocalDateTime.now());

        List<Direct> directList = new ArrayList<>(List.of(direct1));


        List<DirectMessage> directMessageList = new ArrayList<>(List.of(directMessage1));

        given(directChannelRepository.findDirectChannelListByCreateMemberIdOrJoinMemberId(any()))
                .willReturn(directList);

        Long unReadCount = 10L;

        directList.forEach(direct -> {

            given(directMessageQueryService.countUnReadMessage(anyString(), anyString()))
                    .willReturn(unReadCount);

            PageRequest pageRequest1 = PageRequest.of(0, 1);

            Slice<DirectMessage> directMessageSlice = new SliceImpl<>(directMessageList, pageRequest1, false);

            given(directMessageRepository.findDirectMessageByRoomIdOrderByCreatedAtDesc(anyString(), any()))
                    .willReturn(directMessageSlice);

            DirectChannelResponseDto directChannelResponseDto = DirectChannelResponseDto.builder()
                    .roomId(direct.getRoomId())
                    .name(direct.getName())
                    .workSpaceId(direct.getId())
                    .createMemberId(direct.getCreateMember().getId())
                    .joinMemberId(direct.getJoinMember().getId())
                    .createMemberName(direct.getCreateMember().getName())
                    .joinMemberName(direct.getJoinMember().getName())
                    .unReadCount(unReadCount)
                    .build();

            if (directMessageSlice.hasContent()) {
                DirectMessage directMessage = directMessageSlice.getContent().get(0);

                directChannelResponseDto.setLastMessage(directMessage.getContent());
                directChannelResponseDto.setMessageCreatedAt(directMessage.getCreatedAt());
            }
            directChannelResponseDtoList.add(directChannelResponseDto);
        });

        List<DirectChannelResponseDto> sortByCreatedAtResponseList = directChannelResponseDtoList.stream()
                .sorted(Comparator.comparing(DirectChannelResponseDto::getMessageCreatedAt).reversed())
                .toList();

        Slice<DirectChannelResponseDto> directChannelResponseDtoSlice = new SliceImpl<>(sortByCreatedAtResponseList, pageRequest, false);
        //when
        Slice<DirectChannelResponseDto> directChannelListResponseDto =
                directChannelService.directChannelSliceSortByMessageCreateDateDesc(email, workSpaceId, pageRequest);

        //then
        assertThat(directChannelListResponseDto).isNotNull();
        assertThat(directChannelListResponseDto.getContent()).
                containsAll(directChannelResponseDtoSlice.getContent());
        assertThat(directChannelListResponseDto.getContent().get(0).getMessageCreatedAt())
                .isEqualTo(directChannelResponseDtoSlice.getContent().get(0).getMessageCreatedAt());
        assertThat(directChannelListResponseDto.getNumber()).isEqualTo(directChannelResponseDtoSlice.getNumber());
        assertThat(directChannelListResponseDto.hasNext()).isEqualTo(directChannelResponseDtoSlice.hasNext());
        assertThat(directChannelListResponseDto.getSize()).isEqualTo(directChannelResponseDtoSlice.getSize());
    }

    @Test
    @DisplayName("[Service] Search Direct Channel Slice Sort By Message CreateDate")
    void searchDirectChannelSliceSortByMessageCreateDate() {
        //given
        String roomId1 = "roomId1";
        String content1 = "content1";
        String workSpaceMemberName = "senderNickName";
        Long readCount = 0L;

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(senderWorkSpaceMember));

        DirectMessage directMessage1 = DirectMessage.createDirectMessage(workSpaceId, roomId1, sender.getId(), sender.getNickname(), content1, readCount, LocalDateTime.now());

        ReflectionTestUtils.setField(directMessage1, "id", UUID.randomUUID().toString());
        ReflectionTestUtils.setField(directMessage1, "createdAt", LocalDateTime.now());

        List<DirectChannelResponseDto> directChannelResponseDtoList = new ArrayList<>();

        Direct direct1 = Direct.createDirectChannel(workSpace, senderWorkSpaceMember, receiverWorkSpaceMember);

        ReflectionTestUtils.setField(direct1, "createdAt", LocalDateTime.now());

        List<Direct> directList = new ArrayList<>(List.of(direct1));


        List<DirectMessage> directMessageList = new ArrayList<>(List.of(directMessage1));


        given(directChannelRepository.findSearchDirectChannelByCreateMemberIdOrJoinMemberId(any(), anyString()))
                .willReturn(directList);

        Long unReadCount = 10L;

        directList.forEach(direct -> {

            given(directMessageQueryService.countUnReadMessage(anyString(), anyString()))
                    .willReturn(unReadCount);

            PageRequest pageRequest1 = PageRequest.of(0, 1);

            Slice<DirectMessage> directMessageSlice = new SliceImpl<>(directMessageList, pageRequest1, false);

            given(directMessageRepository.findDirectMessageByRoomIdOrderByCreatedAtDesc(anyString(), any()))
                    .willReturn(directMessageSlice);

            DirectChannelResponseDto directChannelResponseDto = DirectChannelResponseDto.builder()
                    .roomId(direct.getRoomId())
                    .name(direct.getName())
                    .workSpaceId(direct.getId())
                    .createMemberId(direct.getCreateMember().getId())
                    .joinMemberId(direct.getJoinMember().getId())
                    .createMemberName(direct.getCreateMember().getName())
                    .joinMemberName(direct.getJoinMember().getName())
                    .unReadCount(unReadCount)
                    .build();

            if (directMessageSlice.hasContent()) {
                DirectMessage directMessage = directMessageSlice.getContent().get(0);

                directChannelResponseDto.setLastMessage(directMessage.getContent());
                directChannelResponseDto.setMessageCreatedAt(directMessage.getCreatedAt());
            }
            directChannelResponseDtoList.add(directChannelResponseDto);
        });

        List<DirectChannelResponseDto> sortByCreatedAtResponseList = directChannelResponseDtoList.stream()
                .sorted(Comparator.comparing(DirectChannelResponseDto::getMessageCreatedAt).reversed())
                .toList();

        Slice<DirectChannelResponseDto> directChannelResponseDtoSlice =
                new SliceImpl<>(sortByCreatedAtResponseList, pageRequest, false);

        //when
        Slice<DirectChannelResponseDto> searchDirectChannelListResponseDto =
                directChannelService.searchDirectChannelSliceSortByMessageCreateDateDesc(email, workSpaceId, workSpaceMemberName, pageRequest);

        //then
        assertThat(searchDirectChannelListResponseDto).isNotNull();
        assertThat(searchDirectChannelListResponseDto.getContent()).
                containsAll(directChannelResponseDtoSlice.getContent());
        assertThat(searchDirectChannelListResponseDto.getContent().get(0).getMessageCreatedAt())
                .isEqualTo(directChannelResponseDtoSlice.getContent().get(0).getMessageCreatedAt());
        assertThat(searchDirectChannelListResponseDto.getNumber()).isEqualTo(directChannelResponseDtoSlice.getNumber());
        assertThat(searchDirectChannelListResponseDto.hasNext()).isEqualTo(directChannelResponseDtoSlice.hasNext());
        assertThat(searchDirectChannelListResponseDto.getSize()).isEqualTo(directChannelResponseDtoSlice.getSize());
    }
}