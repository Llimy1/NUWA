package org.project.nuwabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceInfoResponse;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceMemberInfoResponse;
import org.project.nuwabackend.global.exception.DuplicationException;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.project.nuwabackend.global.type.ErrorMessage.MEMBER_ID_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

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

        // 워크스페이스 멤버 중복 확인
        duplicateWorkSpaceMemberName(workSpaceMemberName);

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

    // 워크스페이스 멤버 가입
    @Transactional
    public Long joinWorkSpaceMember(String email, WorkSpaceMemberRequestDto workSpaceMemberRequestDto) {
        log.info("워크스페이스 멤버 가입");
        Long workSpaceId = workSpaceMemberRequestDto.workSpaceId();
        String workSpaceMemberName = workSpaceMemberRequestDto.workSpaceMemberName();
        String workSpaceMemberJob = workSpaceMemberRequestDto.workSpaceMemberJob();
        String workSpaceMemberImage = workSpaceMemberRequestDto.workSpaceMemberImage();

        // 멤버 이름 중복 확인
        duplicateWorkSpaceMemberName(workSpaceMemberName);

        // 멤버 찾기
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MEMBER_ID_NOT_FOUND));

        // 워크스페이스 찾기
        WorkSpace findWorkSpace = workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        WorkSpaceMember workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.JOIN,
                findMember,
                findWorkSpace);

        WorkSpaceMember saveWorkSpaceMember = workSpaceMemberRepository.save(workSpaceMember);

        return saveWorkSpaceMember.getId();
    }

    // 워크스페이스 멤버 중복
    public void duplicateWorkSpaceMemberName(String workSpaceMemberName) {
        log.info("워크스페이스 멤버 이름 중복 확인");
        workSpaceMemberRepository.findByName(workSpaceMemberName)
                .ifPresent(e -> {
                    throw new DuplicationException(WORK_SPACE_NOT_FOUND);
                });
    }



    public List<WorkSpaceInfoResponse> getWorkspacesByMemberEmail(String email) {
        // 멤버 조회
//        Member findMember = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_ID_NOT_FOUND));
        // 해당 멤버가 속한 워크스페이스 멤버 조회
        //List<WorkSpaceMember> workSpaceMembers = workSpaceMemberRepository.findByMember(findMember);
        List<WorkSpaceMember> workSpaceMembers = workSpaceMemberRepository.findByWorkSpaceList(email);
//        for (WorkSpaceMember workSpaceMember : workSpaceMembers) {
//            System.out.println(workSpaceMember.getWorkSpace().getName());
//        }
//        return null;
////
        // 조회된 워크스페이스멤버로부터 워크스페이스 정보 추출
        return workSpaceMembers.stream()
                .map(WorkSpaceMember::getWorkSpace)
                .map(workSpace -> WorkSpaceInfoResponse.builder()
                        .workspaceId(workSpace.getId())
                        .workSpaceName(workSpace.getName())
                        .workSpaceImage(workSpace.getImage())
                        .workSpaceIntroduce(workSpace.getIntroduce()).build())
                .collect(Collectors.toList());

    }

    public List<WorkSpace> findWorkspacesByMemberEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
        return workSpaceMemberRepository.findWorkSpacesByMember(member);
    }

    public List<WorkSpaceMemberInfoResponse> getAllMembersByWorkspace(Long workSpaceId) {
        // 워크스페이스 찾기
        WorkSpace findWorkSpace = workSpaceRepository.findById(workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        // 워크스페이스로 워크스페이스 멤버 찾기
        List<WorkSpaceMember> workSpaceMembers = workSpaceMemberRepository.findByWorkSpace(findWorkSpace);

        // WorkSpaceMemberInfoResponse list dto로 변환
        return workSpaceMembers.stream().map(member -> WorkSpaceMemberInfoResponse.builder()
                        .name(member.getName())
                        .job(member.getJob())
                        .image(member.getImage())
                        .workSpaceMemberType(member.getWorkSpaceMemberType())
                        .email(member.getMember().getEmail())
                        .nickname(member.getMember().getNickname())
                        .build())
                .collect(Collectors.toList());
    }
}
