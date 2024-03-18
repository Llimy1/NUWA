package org.project.nuwabackend.service.workspace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.service.channel.ChatChannelService;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.project.nuwabackend.service.message.ChatMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.notification.NotificationService;
import org.project.nuwabackend.service.s3.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.WORK_SPACE_QUIT_SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkSpaceDeleteService {

    private final DirectMessageQueryService directMessageQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final DirectChannelService directChannelService;
    private final NotificationService notificationService;
    private final ChatChannelService chatChannelService;
    private final WorkSpaceService workSpaceService;
    private final FileService fileService;

    // 워크스페이스 삭제
    // TODO: integrated test code
    @Transactional
    public String deleteWorkSpace(String email, Long workSpaceId) {

        Integer memberCount = workSpaceService.quitWorkSpaceMember(email, workSpaceId);

        if (memberCount.equals(0)) {
            directMessageQueryService.deleteDirectMessageWorkSpaceId(workSpaceId);
            chatMessageQueryService.deleteChatMessageWorkSpaceId(workSpaceId);
            fileService.deleteFileWorkSpaceId(workSpaceId);
            chatChannelService.deleteChatJoinMemberByWorkSpaceId(workSpaceId);
            chatChannelService.deleteChatChannelList(workSpaceId);
            directChannelService.deleteDirectChannelList(workSpaceId);
            notificationService.deleteNotificationWorkSpaceId(workSpaceId);
            workSpaceService.deleteWorkSpaceMember(workSpaceId);
            workSpaceService.deleteWorkSpace(workSpaceId);
            return DELETE_WORK_SPACE_SUCCESS.getMessage();
        } else {
            return WORK_SPACE_QUIT_SUCCESS.getMessage();
        }
    }
}
