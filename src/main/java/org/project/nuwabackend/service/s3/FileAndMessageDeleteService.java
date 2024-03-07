package org.project.nuwabackend.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.service.message.ChatMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO: test code
public class FileAndMessageDeleteService {

    private final DirectMessageQueryService directMessageQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final FileService fileService;

    // 파일과 다이렉트 메세지 삭제
    @Transactional
    public void deleteFileAndDirectMessage(Long workSpaceId, Long fileId) {
        String fileUrl = fileService.deleteFile(fileId);
        directMessageQueryService.deleteDirectMessageByFile(workSpaceId, fileUrl);
    }

    // 파일과 채팅 메세지 삭제
    @Transactional
    public void deleteFileAndChatMessage(Long workSpaceId, Long fileId) {
        String fileUrl = fileService.deleteFile(fileId);
        chatMessageQueryService.deleteChatMessageByFile(workSpaceId, fileUrl);
    }

}
