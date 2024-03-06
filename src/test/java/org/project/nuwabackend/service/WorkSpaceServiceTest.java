package org.project.nuwabackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.response.IndividualWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.global.exception.DuplicationException;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.service.workspace.WorkSpaceService;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.test.util.ReflectionTestUtils;

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


@DisplayName("[Service] WorkSpace Service Test")
@ExtendWith(MockitoExtension.class)
class WorkSpaceServiceTest {

    @Mock
    WorkSpaceRepository workSpaceRepository;
    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    MemberRepository memberRepository;

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

        workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.CREATED, member, workSpace);
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
                WorkSpaceMemberType.JOIN,
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
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
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
                WorkSpaceMember.joinWorkSpaceMember("name", "N", WorkSpaceMemberType.JOIN, member, workSpace);

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
}