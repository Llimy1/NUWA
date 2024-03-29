package org.project.nuwabackend.nuwa.delete.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.canvas.service.CanvasService;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelService;
import org.project.nuwabackend.nuwa.channel.service.DirectChannelService;
import org.project.nuwabackend.nuwa.websocket.service.ChatMessageQueryService;
import org.project.nuwabackend.nuwa.websocket.service.DirectMessageQueryService;
import org.project.nuwabackend.nuwa.notification.service.NotificationService;
import org.project.nuwabackend.nuwa.file.service.FileService;
import org.project.nuwabackend.nuwa.workspacemember.service.WorkSpaceMemberService;
import org.project.nuwabackend.nuwa.workspace.service.WorkSpaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.response.type.SuccessMessage.DELETE_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_QUIT_SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkSpaceDeleteService {

    private final DirectMessageQueryService directMessageQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final WorkSpaceMemberService workSpaceMemberService;
    private final DirectChannelService directChannelService;
    private final NotificationService notificationService;
    private final ChatChannelService chatChannelService;
    private final WorkSpaceService workSpaceService;
    private final CanvasService canvasService;
    private final FileService fileService;

    // 워크스페이스 삭제
    @Transactional
    public String deleteWorkSpace(String email, Long workSpaceId) {

        Integer memberCount = workSpaceMemberService.quitWorkSpaceMember(email, workSpaceId);

        if (memberCount.equals(0)) {
            directMessageQueryService.deleteDirectMessageWorkSpaceId(workSpaceId);
            chatMessageQueryService.deleteChatMessageWorkSpaceId(workSpaceId);
            canvasService.deleteCanvasByWorkSpace(workSpaceId);
            fileService.deleteFileWorkSpaceId(workSpaceId);
            chatChannelService.deleteChatJoinMemberByWorkSpaceId(workSpaceId);
            chatChannelService.deleteChatChannelList(workSpaceId);
            directChannelService.deleteDirectChannelList(workSpaceId);
            notificationService.deleteNotificationWorkSpaceId(workSpaceId);
            workSpaceMemberService.deleteWorkSpaceMember(workSpaceId);
            workSpaceService.deleteWorkSpace(workSpaceId);
            return DELETE_WORK_SPACE_SUCCESS.getMessage();
        } else {
            return WORK_SPACE_QUIT_SUCCESS.getMessage();
        }
    }
}
