package org.project.nuwabackend.nuwa.workspacemember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.workspacemember.dto.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.nuwa.workspacemember.dto.request.WorkSpaceMemberUpdateRequestDto;
import org.project.nuwabackend.global.exception.custom.DuplicationException;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.workspace.repository.WorkSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.project.nuwabackend.global.response.type.ErrorMessage.DUPLICATE_EMAIL;
import static org.project.nuwabackend.global.response.type.ErrorMessage.MEMBER_ID_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_NOT_FOUND;
import static org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType.JOIN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceMemberService {


    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final MemberRepository memberRepository;

    // 워크스페이스 멤버 가입
    @Transactional
    public Long joinWorkSpaceMember(String email, WorkSpaceMemberRequestDto workSpaceMemberRequestDto) {
        log.info("워크스페이스 멤버 가입");
        Long workSpaceId = workSpaceMemberRequestDto.workSpaceId();
        int index = email.indexOf("@");
        String emailSub = email.substring(0, index);
        String workSpaceMemberImage = workSpaceMemberRequestDto.workSpaceMemberImage();

        // 재참가 로직
        Optional<WorkSpaceMember> optionalWorkSpaceMember =
                workSpaceMemberRepository.findByDeleteMemberEmailAndWorkSpaceId(email, workSpaceId);

        if (optionalWorkSpaceMember.isPresent()) {
            WorkSpaceMember workSpaceMember = optionalWorkSpaceMember.get();
            workSpaceMember.reJoinWorkSpaceMember();
            WorkSpace findWorkSpace = workSpaceMember.getWorkSpace();
            findWorkSpace.increaseWorkSpaceMemberCount();

            return workSpaceMember.getId();
        }

        // 멤버 이메일 중복 확인
        duplicateWorkSpaceMemberEmail(email, workSpaceId);

        // 멤버 찾기
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MEMBER_ID_NOT_FOUND));

        // 워크스페이스 찾기
        WorkSpace findWorkSpace = workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        WorkSpaceMember workSpaceMember = WorkSpaceMember.joinWorkSpaceMember(
                emailSub,
                workSpaceMemberImage,
                JOIN,
                findMember,
                findWorkSpace);

        WorkSpaceMember saveWorkSpaceMember = workSpaceMemberRepository.save(workSpaceMember);

        findWorkSpace.increaseWorkSpaceMemberCount();

        return saveWorkSpaceMember.getId();
    }

    // 워크스페이스 멤버 이름 중복
    public void duplicateWorkSpaceMemberName(String workSpaceMemberName) {
        log.info("워크스페이스 멤버 이름 중복 확인");
        workSpaceMemberRepository.findByName(workSpaceMemberName)
                .ifPresent(e -> {
                    throw new DuplicationException(WORK_SPACE_NOT_FOUND);
                });
    }

    // 워크스페이스 멤버 이메일 중복
    public void duplicateWorkSpaceMemberEmail(String email, Long workSpaceId) {
        log.info("워크스페이스 멤버 이메일 중복 확인");
        workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .ifPresent(e -> {
                    throw new DuplicationException(DUPLICATE_EMAIL);
                });
    }

    // 워크스페이스 멤버 정보 편집
    @Transactional
    public void updateWorkSpaceMember(String email, Long workSpaceId, WorkSpaceMemberUpdateRequestDto workSpaceMemberUpdateRequestDto) {
        log.info("워크스페이스 멤버 편집");
        String updateName = workSpaceMemberUpdateRequestDto.workSpaceMemberName();
        String updateJob = workSpaceMemberUpdateRequestDto.workSpaceMemberJob();
        String updateImage = workSpaceMemberUpdateRequestDto.workSpaceMemberImage();

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        findWorkSpaceMember.updateWorkSpaceMember(updateName, updateJob, updateImage);
    }

    // 워크스페이스 멤버 나가기
    @Transactional
    public Integer quitWorkSpaceMember(String email, Long workSpaceId) {
        WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        workSpaceMember.deleteWorkSpaceMember();

        WorkSpace workSpace = workSpaceMember.getWorkSpace();
        workSpace.decreaseWorkSpaceMemberCount();
        return workSpace.getCount();
    }

    // 워크스페이스 id에 해당하는 멤버 전부 삭제
    @Transactional
    public void deleteWorkSpaceMember(Long workSpaceId) {
        workSpaceMemberRepository.deleteByWorkSpaceId(workSpaceId);
    }
}
