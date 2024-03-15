package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.service.message.ChatMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.s3.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;

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
    public void deleteChatChannelAndChatMessage(Long workSpaceId, String email, String roomId) {
        fileService.deleteFileByWorkSpaceIdAndRoomId(workSpaceId, roomId);
        chatMessageQueryService.deleteWorkSpaceIdAndRoomId(workSpaceId, roomId);
        chatChannelService.deleteChatChannel(workSpaceId, email, roomId);
    }

    @Transactional
    public void deleteDirectChannelAndDirectMessage(Long workSpaceId, String email, String roomId) {
        Direct direct = directChannelService.deleteChannelMember(workSpaceId, email, roomId);

        if (direct != null) {
            fileService.deleteFileByWorkSpaceIdAndRoomId(workSpaceId, roomId);
            directMessageQueryService.deleteDirectMessageByWorkSpaceIdAndRoomId(workSpaceId, roomId);
            directChannelRepository.delete(direct);
        } else {
            throw new NotFoundException(CHANNEL_NOT_FOUND);
        }
    }
}
