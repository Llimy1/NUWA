package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.service.message.ChatMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.s3.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_CHAT_CHANNEL_AND_CHAT_MESSAGE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_DIRECT_CHANNEL_AND_DIRECT_MESSAGE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.QUIT_CHAT_CHANNEL_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.QUIT_DIRECT_CHANNEL_SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO: integrated test code
public class ChannelAndMessageDeleteService {

    private final DirectChannelRepository directChannelRepository;

    private final DirectMessageQueryService directMessageQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final DirectChannelService directChannelService;
    private final ChatChannelService chatChannelService;
    private final FileService fileService;

    @Transactional
    public String deleteChatChannelAndChatMessage(Long workSpaceId, String email, Long channelId) {

        String roomId = chatChannelService.chatChannelQuit(email, workSpaceId, channelId);

        if (roomId != null) {
            fileService.deleteFileByWorkSpaceIdAndRoomId(workSpaceId, roomId);
            chatMessageQueryService.deleteWorkSpaceIdAndRoomId(workSpaceId, roomId);
            chatChannelService.deleteChatJoinMember(roomId);
            chatChannelService.deleteChatChannel(workSpaceId, roomId);
            return DELETE_CHAT_CHANNEL_AND_CHAT_MESSAGE_SUCCESS.getMessage();
        } else {
            return QUIT_CHAT_CHANNEL_SUCCESS.getMessage();
        }
    }

    @Transactional
    public String deleteDirectChannelAndDirectMessage(Long workSpaceId, String email, String roomId) {
        Direct direct = directChannelService.deleteChannelMember(workSpaceId, email, roomId);

        if (direct != null) {
            fileService.deleteFileByWorkSpaceIdAndRoomId(workSpaceId, roomId);
            directMessageQueryService.deleteDirectMessageByWorkSpaceIdAndRoomId(workSpaceId, roomId);
            directChannelRepository.delete(direct);
            return DELETE_DIRECT_CHANNEL_AND_DIRECT_MESSAGE_SUCCESS.getMessage();
        } else {
            return QUIT_DIRECT_CHANNEL_SUCCESS.getMessage();
        }
    }
}
