package org.project.nuwabackend.nuwa.workspace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.workspace.dto.request.WorkSpaceRequestDto;
import org.project.nuwabackend.nuwa.workspace.dto.request.WorkSpaceUpdateRequestDto;
import org.project.nuwabackend.global.exception.custom.DuplicationException;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.global.response.type.ErrorMessage;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.notification.service.NotificationService;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.workspace.repository.WorkSpaceRepository;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.response.type.ErrorMessage.DUPLICATE_WORK_SPACE_NAME;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_ALREADY_CREATE_TYPE;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_ALREADY_JOIN_TYPE;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_TYPE_EQUAL_CREATE;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_NOT_CREATED_MEMBER;
import static org.project.nuwabackend.nuwa.notification.type.NotificationType.NOTICE;
import static org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType.CREATED;
import static org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType.JOIN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final MemberRepository memberRepository;

    private final NotificationService notificationService;

    @Transactional
    public Long createWorkSpace(String email, WorkSpaceRequestDto workSpaceRequestDto) {
        log.info("워크스페이스 생성 서비스");
        String workSpaceName = workSpaceRequestDto.workSpaceName();
        String workSpaceImage = workSpaceRequestDto.workSpaceImage();
        String workSpaceIntroduce = workSpaceRequestDto.workSpaceIntroduce();
        String workSpaceMemberName = workSpaceRequestDto.workSpaceMemberName();
        String workSpaceMemberJob = workSpaceRequestDto.workSpaceMemberJob();
        String workSpaceMemberImage = workSpaceRequestDto.workSpaceMemberImage();

        // 워크스페이스 이름 중복
        duplicateWorkSpaceName(workSpaceName);

        // 워크스페이스 생성
        WorkSpace workSpace =
                WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        WorkSpace saveWorkSpace = workSpaceRepository.save(workSpace);

        // 멤버 조회
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_ID_NOT_FOUND));

        // 워크스페이스 멤버 생성 (Create)
        WorkSpaceMember createWorkSpaceMember = WorkSpaceMember.createWorkSpaceMember(workSpaceMemberName, workSpaceMemberJob,
                workSpaceMemberImage, CREATED,
                findMember, saveWorkSpace);

        workSpaceMemberRepository.save(createWorkSpaceMember);

        saveWorkSpace.increaseWorkSpaceMemberCount();

        return saveWorkSpace.getId();
    }

    // 워크스페이스 이름 중복
    public void duplicateWorkSpaceName(String workSpaceName) {
        log.info("워크스페이스 이름 중복 확인");
        workSpaceRepository.findByName(workSpaceName)
                .ifPresent(e -> {
                    throw new DuplicationException(DUPLICATE_WORK_SPACE_NAME);
                });
    }

    // 워크스페이스 정보 편집
    @Transactional
    public void updateWorkSpace(String email, Long workSpaceId, WorkSpaceUpdateRequestDto workSpaceUpdateRequestDto) {
        log.info("워크스페이스 편집");
        String updateName = workSpaceUpdateRequestDto.workSpaceName();
        String updateImage = workSpaceUpdateRequestDto.workSpaceImage();

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        if (!findWorkSpaceMember.getWorkSpaceMemberType().equals(CREATED)) throw new IllegalArgumentException(WORK_SPACE_NOT_CREATED_MEMBER.getMessage());

        WorkSpace findWorkSpace = findWorkSpaceMember.getWorkSpace();

        findWorkSpace.updateWorkSpace(updateName, updateImage);
    }

    // 워크스페이스 상태 편집
    @Transactional
    public void updateWorkSpaceMemberStatus(String email, Long workSpaceId, String workSpaceMemberStatus) {
        WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        workSpaceMember.updateWorkSpaceMemberStatus(workSpaceMemberStatus);
    }

    // 워크스페이스 권한 넘기기
    @Transactional
    public void relocateCreateWorkSpaceMemberType(Long workSpaceMemberId, String email, Long workSpaceId, WorkSpaceMemberType type) {

        WorkSpaceMember createWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpaceMember joinWorkSpaceMember = workSpaceMemberRepository.findById(workSpaceMemberId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpaceMemberType createWorkSpaceMemberType = createWorkSpaceMember.getWorkSpaceMemberType();
        WorkSpaceMemberType joinWorkSpaceMemberType = joinWorkSpaceMember.getWorkSpaceMemberType();

        if (!createWorkSpaceMemberType.equals(CREATED)) {
            throw new IllegalArgumentException(WORK_SPACE_ALREADY_JOIN_TYPE.getMessage());
        }

        String workSpaceMemberName = joinWorkSpaceMember.getName();
        if (type.equals(CREATED)) {
            if (joinWorkSpaceMemberType.equals(CREATED)) {
                throw new IllegalArgumentException(WORK_SPACE_ALREADY_CREATE_TYPE.getMessage());
            }

            joinWorkSpaceMember.updateCreateWorkSpaceMemberType();
            notificationService.send(workSpaceMemberName + "님이 워크스페이스 소유주로 변경되었습니다.",
                    createWorkSpaceUrl(workSpaceId), NOTICE, createWorkSpaceMember, joinWorkSpaceMember);
        } else {
            if (!joinWorkSpaceMemberType.equals(JOIN)) {
                throw new IllegalArgumentException(WORK_SPACE_MEMBER_TYPE_EQUAL_CREATE.getMessage());
            }

            joinWorkSpaceMember.updateJoinWorkSpaceMemberType();
            notificationService.send(workSpaceMemberName + "님이 워크스페이스 멤버로 변경되었습니다.",
                    createWorkSpaceUrl(workSpaceId), NOTICE, createWorkSpaceMember, joinWorkSpaceMember);
        }
    }

    @Transactional
    public void deleteWorkSpace(Long workSpaceId) {
        workSpaceRepository.deleteById(workSpaceId);
    }

    private String createWorkSpaceUrl(Long workSpaceId) {
        return "http://localhost:3000/workspace/" + workSpaceId;
    }
}
