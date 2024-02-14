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
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;


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
    private Member member;



    @BeforeEach
    void setup() {
        String nickname = "nickname";
        String email = "email";
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
    }

    @Test
    @DisplayName("[Service] Create WorkSpace And WorkSpaceMember Test")
    void createWorkSpaceAndWorkSpaceMember() {
        //given
        String email = "email";

        WorkSpace workSpace = WorkSpace.createWorkSpace(workSpaceRequestDto.workSpaceName(),
                workSpaceRequestDto.workSpaceImage(), workSpaceRequestDto.workSpaceIntroduce());
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
        assertThat(workSpaceId).isEqualTo(1L);
    }
}