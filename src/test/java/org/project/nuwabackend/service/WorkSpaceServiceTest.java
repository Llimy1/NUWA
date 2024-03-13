package org.project.nuwabackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.notification.Notification;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.response.FavoriteWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.dto.workspace.response.IndividualWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.global.exception.DuplicationException;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.notification.NotificationService;
import org.project.nuwabackend.service.workspace.WorkSpaceService;
import org.project.nuwabackend.type.NotificationType;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.project.nuwabackend.global.type.ErrorMessage.DUPLICATE_EMAIL;
import static org.project.nuwabackend.global.type.ErrorMessage.DUPLICATE_WORK_SPACE_NAME;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_CREATED_MEMBER;
import static org.project.nuwabackend.type.NotificationType.NOTICE;
import static org.project.nuwabackend.type.WorkSpaceMemberType.CREATED;
import static org.project.nuwabackend.type.WorkSpaceMemberType.JOIN;


@DisplayName("[Service] WorkSpace Service Test")
@ExtendWith(MockitoExtension.class)
class WorkSpaceServiceTest {

    @Mock
    WorkSpaceRepository workSpaceRepository;
    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    DirectChannelRepository directChannelRepository;
    @Mock
    DirectMessageQueryService directMessageQueryService;
    @Mock
    NotificationService notificationService;

    @InjectMocks
    WorkSpaceService workSpaceService;

    private WorkSpaceRequestDto workSpaceRequestDto;
    private WorkSpaceMemberRequestDto workSpaceMemberRequestDto;
    private Member member;
    private WorkSpace workSpace;
    private WorkSpaceMember workSpaceMember;

    String email = "abcd@gmail.com";
    Long workspaceId = 1L;


    @BeforeEach
    void setup() {
        String nickname = "nickname";

        String password = "password";
        String phoneNumber = "01000000000";

        member = Member.createMember(email, password, nickname, phoneNumber);

        String workSpaceName = "workSpaceName";
        String workSpaceImage = "workSpaceImage";
        String workSpaceIntroduce = "workSpaceIntroduce";
        String workSpaceMemberName = "workSpaceMemberName";
        String workSpaceMemberJob = "workSpaceMemberJob";
        String workSpaceMemberImage = "workSpaceMemberImage";


        workSpaceRequestDto =
                new WorkSpaceRequestDto(
                        workSpaceName, workSpaceImage,
                        workSpaceIntroduce, workSpaceMemberName,
                        workSpaceMemberJob, workSpaceMemberImage);

        workSpaceMemberRequestDto =
                new WorkSpaceMemberRequestDto(workspaceId, workSpaceMemberImage);


        workSpace = WorkSpace.createWorkSpace(workSpaceRequestDto.workSpaceName(),
                workSpaceRequestDto.workSpaceImage(), workSpaceRequestDto.workSpaceIntroduce());
        ReflectionTestUtils.setField(workSpace, "id", 1L);

        workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.CREATED, member, workSpace);
        ReflectionTestUtils.setField(workSpaceMember, "id", 1L);
    }

    @Test
    @DisplayName("[Service] Create WorkSpace And WorkSpaceMember Test")
    void createWorkSpaceAndSaveWorkSpaceMemberSuccess() {
        //given
        WorkSpaceMember workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceRequestDto.workSpaceMemberName(), workSpaceRequestDto.workSpaceMemberJob(),
                workSpaceRequestDto.workSpaceImage(), WorkSpaceMemberType.CREATED, member, workSpace);

        given(workSpaceRepository.save(any()))
                .willReturn(workSpace);
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(workSpaceMemberRepository.save(any()))
                .willReturn(workSpaceMember);

        ReflectionTestUtils.setField(workSpace, "id", 1L);

        //when
        Long workSpaceId = workSpaceService.createWorkSpace(email, workSpaceRequestDto);

        //then
        assertThat(workSpaceId).isEqualTo(workSpace.getId());
        assertThat(workSpace.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("[Service] Duplicate WorkSpace Name")
    void createWorkSpaceAndSaveWorkSpaceMemberFailByDuplicateWorkSpaceName() {
        //given
        given(workSpaceRepository.findByName(anyString()))
                .willThrow(new DuplicationException(DUPLICATE_WORK_SPACE_NAME));

        //when
        //then
        assertThatThrownBy(() -> workSpaceService.duplicateWorkSpaceName(workSpaceRequestDto.workSpaceName()))
                .isInstanceOf(DuplicationException.class);
    }

    @Test
    @DisplayName("[Service] Join WorkSpace Member Test")
    void joinWorkSpaceMemberTest() {
        //given
        int index = email.indexOf("@");
        String emailSub = email.substring(0, index);

        WorkSpaceMember workSpaceMember = WorkSpaceMember.joinWorkSpaceMember(
                emailSub,
                workSpaceMemberRequestDto.workSpaceMemberImage(),
                JOIN,
                member,
                workSpace);

        ReflectionTestUtils.setField(workSpaceMember, "id", 1L);
        given(workSpaceRepository.findById(any()))
                .willReturn(Optional.of(workSpace));
        given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));
        given(workSpaceMemberRepository.save(any()))
                .willReturn(workSpaceMember);

        //when
        Long workSpaceMemberId = workSpaceService.joinWorkSpaceMember(member.getEmail(), workSpaceMemberRequestDto);

        //then
        assertThat(workSpaceMemberId).isEqualTo(workSpaceMember.getId());
        assertThat(workSpace.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("[Service] Duplicate WorkSpace Name")
    void joinWorkSpaceAndSaveWorkSpaceMemberFailByDuplicateWorkSpaceMemberEmail() {
        //given
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willThrow(new DuplicationException(DUPLICATE_EMAIL));

        //when
        //then
        assertThatThrownBy(() -> workSpaceService.duplicateWorkSpaceMemberEmail(email, workSpace.getId()))
                .isInstanceOf(DuplicationException.class);
    }

    @Test
    @DisplayName("[Service] Individual WorkSpace Member Info Test")
    void individualWorkSpaceMemberInfoTest() {
        //given
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(workSpaceMember));

        IndividualWorkSpaceMemberInfoResponseDto individualWorkSpaceMemberInfo = IndividualWorkSpaceMemberInfoResponseDto.builder()
                .id(workSpaceMember.getId())
                .name(workSpaceMember.getName())
                .image(workSpaceMember.getImage())
                .job(workSpaceMember.getJob())
                .status(workSpaceMember.getStatus())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .isDelete(workSpaceMember.getIsDelete())
                .build();
        //when
        IndividualWorkSpaceMemberInfoResponseDto individualWorkSpaceMemberInfoResponseDto =
                workSpaceService.individualWorkSpaceMemberInfo(email, workspaceId);

        //then
        assertThat(individualWorkSpaceMemberInfoResponseDto.id()).isEqualTo(individualWorkSpaceMemberInfo.id());
        assertThat(individualWorkSpaceMemberInfoResponseDto.name()).isEqualTo(individualWorkSpaceMemberInfo.name());
        assertThat(individualWorkSpaceMemberInfoResponseDto.job()).isEqualTo(individualWorkSpaceMemberInfo.job());
        assertThat(individualWorkSpaceMemberInfoResponseDto.image()).isEqualTo(individualWorkSpaceMemberInfo.image());
        assertThat(individualWorkSpaceMemberInfoResponseDto.email()).isEqualTo(individualWorkSpaceMemberInfo.email());
        assertThat(individualWorkSpaceMemberInfoResponseDto.phoneNumber()).isEqualTo(individualWorkSpaceMemberInfo.phoneNumber());
    }

    @Test
    @DisplayName("[Service] Update WorkSpace Test")
    void updateWorkSpaceTest() {
        //given

        String updateWorkSpaceName = "updateWorkSpaceName";
        String updateWorkSpaceImage = "updateWorkSpaceImage";

        WorkSpaceUpdateRequestDto workSpaceUpdateRequestDto = new WorkSpaceUpdateRequestDto(updateWorkSpaceName, updateWorkSpaceImage);

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workspaceId))
                .willReturn(Optional.of(workSpaceMember));
        //when
        workSpaceService.updateWorkSpace(email, workspaceId, workSpaceUpdateRequestDto);

        //then
        assertThat(workSpace.getName()).isEqualTo(updateWorkSpaceName);
        assertThat(workSpace.getImage()).isEqualTo(updateWorkSpaceImage);
    }

    @Test
    @DisplayName("[Service] Update WorkSpace Fail Test")
    void updateWorkSpaceFailTest() {
        //given
        String updateWorkSpaceName = "updateWorkSpaceName";
        String updateWorkSpaceImage = "updateWorkSpaceImage";

        WorkSpaceUpdateRequestDto workSpaceUpdateRequestDto = new WorkSpaceUpdateRequestDto(updateWorkSpaceName, updateWorkSpaceImage);


        WorkSpaceMember joinWorkSpaceMember =
                WorkSpaceMember.joinWorkSpaceMember("name", "N", JOIN, member, workSpace);

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(joinWorkSpaceMember));

        //when
        //then
        assertThatThrownBy(() -> workSpaceService.updateWorkSpace(email, workspaceId, workSpaceUpdateRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(WORK_SPACE_NOT_CREATED_MEMBER.getMessage());
    }

    @Test
    @DisplayName("[Service] Update WorkSpace Member Test")
    void updateWorkSpaceMemberTest() {
        //given
        String updateWorkSpaceMemberName = "updateName";
        String updateWorkSpaceMemberJob = "updateJob";
        String updateWorkSpaceMemberImage = "updateImage";

        WorkSpaceMemberUpdateRequestDto workSpaceMemberUpdateRequestDto =
                new WorkSpaceMemberUpdateRequestDto(updateWorkSpaceMemberName, updateWorkSpaceMemberJob, updateWorkSpaceMemberImage);

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workspaceId))
                .willReturn(Optional.of(workSpaceMember));

        //when
        workSpaceService.updateWorkSpaceMember(email, workspaceId, workSpaceMemberUpdateRequestDto);

        //then
        assertThat(workSpaceMember.getName()).isEqualTo(updateWorkSpaceMemberName);
        assertThat(workSpaceMember.getJob()).isEqualTo(updateWorkSpaceMemberJob);
        assertThat(workSpaceMember.getImage()).isEqualTo(updateWorkSpaceMemberImage);
    }

    @Test
    @DisplayName("[Service] favorite WorkSpace Member List Test")
    void favoriteWorkSpaceMemberListTest() {
        //given
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(workSpaceMember));

        String email1 = "abcde@gmail.com";
        String nickname1 = "nickname";
        String password1 = "password";
        String phoneNumber1 = "01000000000";

        Member member1 = Member.createMember(email1, password1, nickname1, phoneNumber1);

        String workSpaceMemberName = "workSpaceMemberName";
        String workSpaceMemberImage = "workSpaceMemberImage";

        WorkSpaceMember other = WorkSpaceMember.joinWorkSpaceMember(workSpaceMemberName, workSpaceMemberImage, JOIN, member1, workSpace);
        ReflectionTestUtils.setField(other, "id", 2L);

        Direct directChannel =
                Direct.createDirectChannel(workSpace, workSpaceMember, other);

        List<Direct> directList = new ArrayList<>(List.of(directChannel));

        given(directChannelRepository.findDirectChannelListByCreateMemberIdOrJoinMemberId(workspaceId))
                .willReturn(directList);

        Long count = 10L;
        Long otherId = 2L;
        List<FavoriteWorkSpaceMemberInfoResponseDto> favoriteWorkSpaceMemberInfoResponseDtoList = new ArrayList<>();
        directList.forEach(direct -> {
                given(directMessageQueryService.countManyMessageSenderId(anyString(), anyString(), any()))
                        .willReturn(count);
                given(directMessageQueryService.neSenderId(anyString(), anyString(), any()))
                        .willReturn(otherId);
                given(workSpaceMemberRepository.findById(any()))
                        .willReturn(Optional.of(other));

                Member otherMember = other.getMember();

            FavoriteWorkSpaceMemberInfoResponseDto favoriteWorkSpaceMemberInfoResponseDto = FavoriteWorkSpaceMemberInfoResponseDto.builder()
                    .id(otherId)
                    .name(other.getName())
                    .job(other.getJob())
                    .image(other.getImage())
                    .workSpaceMemberType(other.getWorkSpaceMemberType())
                    .email(otherMember.getEmail())
                    .phoneNumber(otherMember.getPhoneNumber())
                    .messageCount(count)
                    .build();

            favoriteWorkSpaceMemberInfoResponseDtoList.add(favoriteWorkSpaceMemberInfoResponseDto);
            }
        );

        List<FavoriteWorkSpaceMemberInfoResponseDto> list = favoriteWorkSpaceMemberInfoResponseDtoList.stream()
                .sorted(Comparator.comparing(FavoriteWorkSpaceMemberInfoResponseDto::messageCount, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        //when
        List<FavoriteWorkSpaceMemberInfoResponseDto> favoriteWorkSpaceMemberInfoResponseDtos =
                workSpaceService.favoriteWorkSpaceMemberList(email1, workspaceId);

        //then
        assertThat(favoriteWorkSpaceMemberInfoResponseDtos).containsAll(list);
        assertThat(favoriteWorkSpaceMemberInfoResponseDtos.get(0).email())
                .isEqualTo(list.get(0).email());
    }

    @Test
    @DisplayName("[Service] Update WorkSpace Member Status Test")
    void updateWorkSpaceMemberStatusTest() {
        //given
        String workSpaceMemberStatus = "rest";

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workspaceId))
                .willReturn(Optional.of(workSpaceMember));

        //when
        workSpaceService.updateWorkSpaceMemberStatus(email, workspaceId, workSpaceMemberStatus);

        //then
        assertThat(workSpaceMember.getStatus()).isEqualTo(workSpaceMemberStatus);
    }

    @Test
    @DisplayName("[Service] Relocate Create WorkSpace Member Type Test")
    void relocateCreateWorkSpaceMemberTypeTest() {
        //given

        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(workSpaceMember));

        Member member1 =
                Member.createMember("abcde@gmail.com", "abcd1234", "a", "0100000000");
        WorkSpaceMember workSpaceMember1 =
                WorkSpaceMember.createWorkSpaceMember("abcd", "abb", "N", JOIN, member1, workSpace);
        ReflectionTestUtils.setField(workSpaceMember1, "id", 2L);

        given(workSpaceMemberRepository.findById(any()))
                .willReturn(Optional.of(workSpaceMember1));

        //when
        workSpaceService.relocateCreateWorkSpaceMemberType(workSpaceMember1.getId(), email, workspaceId);

        //then
        assertThat(workSpaceMember.getWorkSpaceMemberType()).isEqualTo(JOIN);
        assertThat(workSpaceMember1.getWorkSpaceMemberType()).isEqualTo(CREATED);
    }

    @Test
    @DisplayName("[Service] Quit WorkSpace Member Test")
    void quitWorkSpaceMemberTest() {
        //given
        String email1 = "abcde@gmail.com";
        Member member1 =
                Member.createMember(email1, "abcd1234", "a", "0100000000");
        WorkSpaceMember workSpaceMember1 =
                WorkSpaceMember.joinWorkSpaceMember("a", "N", JOIN, member1, workSpace);
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(workSpaceMember1));

        //when
        workSpaceService.quitWorkSpaceMember(email1, workspaceId);

        //then
        assertThat(workSpaceMember1.getIsDelete()).isTrue();
    }
}