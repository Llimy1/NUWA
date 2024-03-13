package org.project.nuwabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.redis.InvitationLinkRedis;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.dto.InvitationLinkRequest;
import org.project.nuwabackend.dto.workspace.response.WorkSpaceInfoResponse;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.repository.redis.InvitationLinkRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.project.nuwabackend.global.type.ErrorMessage.REDIS_TOKEN_NOT_FOUND_INFO;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationLinkService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final InvitationLinkRedisRepository invitationLinkRedisRepository;
    private final WorkSpaceRepository workSpaceRepository;

    @Transactional
    public String getOrCreateInvitationLink(String email, InvitationLinkRequest invitationLinkRequest) {

        Long workSpaceId = invitationLinkRequest.workSpaceId();

        // 해당 워크스페이스의 멤버인지 확인
        workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        return invitationLinkRedisRepository.findTopByWorkSpaceIdOrderByTokenDesc(workSpaceId)
                .map(invitation -> constructInvitationLink(invitation.getToken()))
                .orElseGet(() -> createNewInvitation(workSpaceId));
    }

    private String createNewInvitation(Long workSpaceId) {
        String token = UUID.randomUUID().toString();
        InvitationLinkRedis invitation = new InvitationLinkRedis(null,token, workSpaceId);
        invitationLinkRedisRepository.save(invitation);

        return constructInvitationLink(token);
    }

    private String constructInvitationLink(String token) {
        // 토큰을 Base64로 인코딩
        String encodedToken = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
        log.info("인코딩" +encodedToken);

        // 인코딩된 토큰을 사용하여 URL 구성
        return "http://localhost:3000/api/invite/" + encodedToken;
    }


    public WorkSpaceInfoResponse getWorkspaceByToken(String token) {
        log.info("초대 링크 조회 서비스");

        // 토큰으로
        InvitationLinkRedis invitationLink = invitationLinkRedisRepository.findFirstByToken(token)
                .orElseThrow(() -> new NotFoundException(REDIS_TOKEN_NOT_FOUND_INFO));


        // 워크스페이스 찾기
        WorkSpace workSpace = workSpaceRepository.findById(invitationLink.getWorkSpaceId())
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_NOT_FOUND));

        return WorkSpaceInfoResponse.builder()
                    .workspaceId(workSpace.getId())
                    .workSpaceName(workSpace.getName())
                    .workSpaceImage(workSpace.getImage())
                    .workSpaceIntroduce(workSpace.getIntroduce()).build();



    }
}
