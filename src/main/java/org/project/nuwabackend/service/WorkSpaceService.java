package org.project.nuwabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createWorkSpace(String email, WorkSpaceRequestDto workSpaceRequestDto) {
        log.info("워크스페이스 생성 서비스");
        String workSpaceName = workSpaceRequestDto.workSpaceName();
        String workSpaceImage = workSpaceRequestDto.workSpaceImage();
        String workSpaceIntroduce = workSpaceRequestDto.workSpaceIntroduce();
        String workSpaceMemberName = workSpaceRequestDto.workSpaceMemberName();
        String workSpaceMemberJob = workSpaceRequestDto.workSpaceMemberJob();
        String workSpaceMemberImage = workSpaceRequestDto.workSpaceMemberImage();

        // 워크스페이스 생성
        WorkSpace workSpace =
                WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        WorkSpace saveWorkSpace = workSpaceRepository.save(workSpace);

        // 멤버 조회
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_ID_NOT_FOUND));

        // 워크스페이스 멤버 생성 (Create)
        WorkSpaceMember createWorkSpaceMember = WorkSpaceMember.createWorkSpaceMember(workSpaceMemberName, workSpaceMemberJob,
                workSpaceMemberImage, WorkSpaceMemberType.CREATED,
                findMember, saveWorkSpace);

        workSpaceMemberRepository.save(createWorkSpaceMember);

        return saveWorkSpace.getId();
    }


}
