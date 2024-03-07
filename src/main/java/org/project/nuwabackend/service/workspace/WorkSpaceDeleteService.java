package org.project.nuwabackend.service.workspace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.service.channel.ChatChannelService;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.project.nuwabackend.service.message.ChatMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.notification.NotificationService;
import org.project.nuwabackend.service.s3.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_ONLY_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkSpaceDeleteService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;

    private final DirectMessageQueryService directMessageQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final DirectChannelService directChannelService;
    private final NotificationService notificationService;
    private final ChatChannelService chatChannelService;
    private final WorkSpaceService workSpaceService;
    private final FileService fileService;

    // 워크스페이스 삭제
    // TODO: test code
    @Transactional
    public void deleteWorkSpace(String email, Long workSpaceId) {
        WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpace workSpace = workSpaceMember.getWorkSpace();

        if (workSpace.getCount().equals(1)) {
            // TODO: 해당 워크스페이스에 존재하는 모든 데이터 삭제
            directMessageQueryService.deleteDirectMessageWorkSpaceId(workSpaceId);
            chatMessageQueryService.deleteChatMessageWorkSpaceId(workSpaceId);
            fileService.deleteFileWorkSpaceId(workSpaceId);
            chatChannelService.deleteChatChannelList(workSpaceId);
            directChannelService.deleteDirectChannelList(workSpaceId);
            notificationService.deleteNotificationWorkSpaceId(workSpaceId);
            workSpaceService.deleteWorkSpaceMember(workSpaceId);
            workSpaceService.deleteWorkSpace(workSpaceId);
        } else {
            throw new IllegalArgumentException(WORK_SPACE_NOT_ONLY_MEMBER.getMessage());
        }
    }
}
