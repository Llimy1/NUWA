package org.project.nuwabackend.nuwa.workspace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.workspace.dto.response.inquiry.FavoriteWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.inquiry.IndividualWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.workspace.WorkSpaceInfoResponse;
import org.project.nuwabackend.nuwa.workspacemember.dto.response.WorkSpaceMemberInfoResponse;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.channel.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.nuwa.domain.channel.Direct;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.websocket.service.DirectMessageQueryService;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.workspace.repository.WorkSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceInquiryService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectMessageQueryService directMessageQueryService;
    private final DirectChannelRepository directChannelRepository;
    private final WorkSpaceRepository workSpaceRepository;


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

    public List<WorkSpaceInfoResponse> getWorkspacesByMemberEmail(String email) {
        // 멤버 조회
        List<WorkSpaceMember> workSpaceMembers = workSpaceMemberRepository.findByWorkSpaceList(email);

        // 조회된 워크스페이스멤버로부터 워크스페이스 정보 추출
        return workSpaceMembers.stream()
                .map(WorkSpaceMember::getWorkSpace)
                .map(workSpace -> WorkSpaceInfoResponse.builder()
                        .workspaceId(workSpace.getId())
                        .workSpaceName(workSpace.getName())
                        .workSpaceImage(workSpace.getImage())
                        .workSpaceIntroduce(workSpace.getIntroduce())
                        .workSpaceMemberCount(workSpace.getCount())
                        .build())
                .collect(Collectors.toList());
    }

    // 개인 별 프로필 조회
    public IndividualWorkSpaceMemberInfoResponseDto individualWorkSpaceMemberInfo(String email, Long workSpaceId) {
        log.info("개인 별 프로필 조회");
        // 워크스페이스 멤버 찾기
        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Member findMember = findWorkSpaceMember.getMember();

        String phoneNumber = findMember.getPhoneNumber();

        return IndividualWorkSpaceMemberInfoResponseDto.builder()
                .id(findWorkSpaceMember.getId())
                .name(findWorkSpaceMember.getName())
                .job(findWorkSpaceMember.getJob())
                .image(findWorkSpaceMember.getImage())
                .status(findWorkSpaceMember.getStatus())
                .phoneNumber(phoneNumber)
                .email(email)
                .isDelete(findWorkSpaceMember.getIsDelete())
                .build();
    }

    // 즐겨 찾는 팀원 조회 (내가 보낸 채팅 수가 가장 많은 순으로 반환)
    public List<FavoriteWorkSpaceMemberInfoResponseDto> favoriteWorkSpaceMemberList(String email, Long workSpaceId) {
        log.info("즐겨 찾는 팀원 조회(내가 보낸 채팅 수가 가장 많은 순)");

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        // 내가 속한 채팅방 리스트 전부 가져오기
        List<Direct> directChannelList =
                directChannelRepository.findDirectChannelListByCreateMemberIdOrJoinMemberId(findWorkSpaceMemberId);

        List<FavoriteWorkSpaceMemberInfoResponseDto> favoriteWorkSpaceMemberInfoResponseDtoList = new ArrayList<>();
        // 채팅방 순회하면서 채팅방 별로 내가 보낸 채팅 개수 가져오기
        directChannelList.forEach(direct -> {

            Long count = directMessageQueryService.countManyMessageSenderId(direct.getRoomId(), email, workSpaceId);

            // 내 아이디로 상대방 id 가져오기
            Long otherId = directMessageQueryService.neSenderId(direct.getRoomId(), email, workSpaceId);

            // 값이 없다면 빈 리스트로 반환
            if (otherId != null) {
                WorkSpaceMember other = workSpaceMemberRepository.findById(otherId)
                        .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

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
        });

        return favoriteWorkSpaceMemberInfoResponseDtoList.stream()
                .sorted(Comparator.comparing(FavoriteWorkSpaceMemberInfoResponseDto::messageCount, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}
