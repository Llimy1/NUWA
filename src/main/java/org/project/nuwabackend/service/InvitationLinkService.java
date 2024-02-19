package org.project.nuwabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.redis.InvitationLinkRedis;
import org.project.nuwabackend.dto.InvitationLinkRequest;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.redis.InvitationLinkRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationLinkService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final InvitationLinkRedisRepository invitationLinkRedisRepository;

    @Transactional
    public String getOrCreateInvitationLink(String email, InvitationLinkRequest invitationLinkRequest) {

        Long workSpaceId = invitationLinkRequest.workSpaceId();

        // 해당 워크스페이스의 멤버인지 확인
        workSpaceMemberRepository.findByWorkSpaceIdAndMemberEmail(workSpaceId, email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        return invitationLinkRedisRepository.findTopByWorkSpaceIdOrderByTokenDesc(workSpaceId)
                .map(invitation -> constructInvitationLink(invitation.getToken()))
                .orElseGet(() -> createNewInvitation(workSpaceId));
    }

    private String createNewInvitation(Long workSpaceId) {
        String token = UUID.randomUUID().toString();
        InvitationLinkRedis invitation = new InvitationLinkRedis(token, workSpaceId);
        invitationLinkRedisRepository.save(invitation);
        return constructInvitationLink(token);
    }

    private String constructInvitationLink(String token) {
        return "http://localhost:8080/invite?token=" + token;
    }
}
