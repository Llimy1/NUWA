package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.service.message.ChatMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.s3.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO: integrated test code
public class ChannelAndMessageDeleteService {

    private final DirectMessageQueryService directMessageQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final DirectChannelService directChannelService;
    private final ChatChannelService chatChannelService;
    private final FileService fileService;

    @Transactional
    public void deleteChatChannelAndChatMessage(Long workSpaceId, String email, String roomId) {
        chatChannelService.deleteChatChannel(workSpaceId, email, roomId);
        chatMessageQueryService.deleteWorkSpaceIdAndRoomId(workSpaceId, roomId);
        fileService.deleteFileByWorkSpaceIdAndRoomId(workSpaceId, roomId);
    }

    @Transactional
    public void deleteDirectChannelAndDirectMessage(Long workSpaceId, String email, String roomId) {
        Boolean flag = directChannelService.deleteChannelMember(workSpaceId, email, roomId);

        if (flag) {
            directMessageQueryService.deleteDirectMessageByWorkSpaceIdAndRoomId(workSpaceId, roomId);
            fileService.deleteFileByWorkSpaceIdAndRoomId(workSpaceId, roomId);
        }
    }
}
