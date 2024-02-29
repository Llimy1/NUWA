package org.project.nuwabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceMemberUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceRequestDto;
import org.project.nuwabackend.dto.workspace.request.WorkSpaceUpdateRequestDto;
import org.project.nuwabackend.dto.workspace.response.FavoriteWorkSpaceMemberInfoResponse;
import org.project.nuwabackend.dto.workspace.response.IndividualWorkSpaceMemberInfoResponse;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceInfoResponse;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceMemberInfoResponse;
import org.project.nuwabackend.global.exception.DuplicationException;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.type.WorkSpaceMemberType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.project.nuwabackend.global.type.ErrorMessage.DUPLICATE_EMAIL;
import static org.project.nuwabackend.global.type.ErrorMessage.MEMBER_ID_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.DUPLICATE_WORK_SPACE_NAME;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_CREATED_MEMBER;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_FOUND;
import static org.project.nuwabackend.type.WorkSpaceMemberType.CREATED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectMessageQueryService directMessageQueryService;
    private final DirectChannelRepository directChannelRepository;
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

        // 워크스페이스 멤버 중복 확인 -> 제거
//        duplicateWorkSpaceMemberName(workSpaceMemberName);

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

        return saveWorkSpace.getId();
    }

    // 워크스페이스 멤버 가입
    @Transactional
    public Long joinWorkSpaceMember(String email, WorkSpaceMemberRequestDto workSpaceMemberRequestDto) {
        log.info("워크스페이스 멤버 가입");
        Long workSpaceId = workSpaceMemberRequestDto.workSpaceId();
        int index = email.indexOf("@");
        String emailSub = email.substring(0, index);
        String workSpaceMemberImage = workSpaceMemberRequestDto.workSpaceMemberImage();

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
                WorkSpaceMemberType.JOIN,
                findMember,
                findWorkSpace);

        WorkSpaceMember saveWorkSpaceMember = workSpaceMemberRepository.save(workSpaceMember);

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

    // 워크스페이스 이름 중복
    public void duplicateWorkSpaceName(String workSpaceName) {
        log.info("워크스페이스 이름 중복 확인");
        workSpaceRepository.findByName(workSpaceName)
                .ifPresent(e -> {
                    throw new DuplicationException(DUPLICATE_WORK_SPACE_NAME);
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
                        .id(member.getId())
                        .name(member.getName())
                        .job(member.getJob())
                        .image(member.getImage())
                        .workSpaceMemberType(member.getWorkSpaceMemberType())
                        .email(member.getMember().getEmail())
                        .nickname(member.getMember().getNickname())
                        .build())
                .collect(Collectors.toList());
    }

    // 개인 별 프로필 조회
    public IndividualWorkSpaceMemberInfoResponse individualWorkSpaceMemberInfo(String email, Long workSpaceId) {
        log.info("개인 별 프로필 조회");
        // 워크스페이스 멤버 찾기
        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Member findMember = findWorkSpaceMember.getMember();

        String phoneNumber = findMember.getPhoneNumber();

        return IndividualWorkSpaceMemberInfoResponse.builder()
                .id(findWorkSpaceMember.getId())
                .name(findWorkSpaceMember.getName())
                .job(findWorkSpaceMember.getJob())
                .image(findWorkSpaceMember.getImage())
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
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

    // 즐겨 찾는 팀원 조회 (내가 보낸 채팅 수가 가장 많은 순으로 반환)
    // TODO: test code
    public List<FavoriteWorkSpaceMemberInfoResponse> favoriteWorkSpaceMemberList(String email, Long workSpaceId) {
        log.info("즐겨 찾는 팀원 조회(내가 보낸 채팅 수가 가장 많은 순)");

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        // 내가 속한 채팅방 리스트 전부 가져오기
        List<Direct> directChannelList =
                directChannelRepository.findDirectChannelListByCreateMemberIdOrJoinMemberId(findWorkSpaceMemberId);

        List<FavoriteWorkSpaceMemberInfoResponse> favoriteWorkSpaceMemberInfoResponseList = new ArrayList<>();
        // 채팅방 순회하면서 채팅방 별로 내가 보낸 채팅 개수 가져오기
        directChannelList.forEach(direct -> {

            Long count = directMessageQueryService.countManyMessageSenderId(direct.getRoomId(), email);

            // 내 아이디로 상대방 id 가져오기
            Long otherId = directMessageQueryService.neSenderId(direct.getRoomId(), email);
            WorkSpaceMember other = workSpaceMemberRepository.findById(otherId)
                    .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

            Member otherMember = other.getMember();

            FavoriteWorkSpaceMemberInfoResponse favoriteWorkSpaceMemberInfoResponse = FavoriteWorkSpaceMemberInfoResponse.builder()
                    .id(otherId)
                    .name(other.getName())
                    .job(other.getJob())
                    .image(other.getImage())
                    .workSpaceMemberType(other.getWorkSpaceMemberType())
                    .email(otherMember.getEmail())
                    .phoneNumber(otherMember.getPhoneNumber())
                    .messageCount(count)
                    .build();

            favoriteWorkSpaceMemberInfoResponseList.add(favoriteWorkSpaceMemberInfoResponse);
        });

        return favoriteWorkSpaceMemberInfoResponseList.stream()
                .sorted(Comparator.comparing(FavoriteWorkSpaceMemberInfoResponse::messageCount))
                .toList();
    }
}
