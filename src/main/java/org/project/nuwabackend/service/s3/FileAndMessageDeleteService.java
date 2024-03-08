package org.project.nuwabackend.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.service.message.ChatMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileAndMessageDeleteService {

    private final DirectMessageQueryService directMessageQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final FileService fileService;

    // 파일과 다이렉트 메세지 삭제
    // TODO: integrated test code
    @Transactional
    public void deleteFileAndDirectMessage(Long workSpaceId, Long fileId) {

        Map<String, String> deleteMap = fileService.deleteFile(fileId);
        deleteMap.forEach((key, value) -> {
            switch (key) {
                case "direct" ->
                        directMessageQueryService.deleteDirectMessageByFile(workSpaceId, value);
                case "chat" ->
                        chatMessageQueryService.deleteChatMessageByFile(workSpaceId, value);
            }
        });
    }
}
