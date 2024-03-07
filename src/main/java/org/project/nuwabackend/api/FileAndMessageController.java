package org.project.nuwabackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.s3.FileAndMessageDeleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_FILE_AND_CHAT_MESSAGE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_FILE_AND_DIRECT_MESSAGE_SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileAndMessageController {

    private final FileAndMessageDeleteService fileAndMessageDeleteService;
    private final GlobalService globalService;

    // TODO : test code
    @DeleteMapping("/file/{workSpaceId}/direct")
    public ResponseEntity<Object> deleteFileAndDirectMessage(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                             @RequestParam(value = "fileId") Long fileId) {
        log.info("다이렉트 파일 & 메세지 삭제 API");
        fileAndMessageDeleteService.deleteFileAndDirectMessage(workSpaceId, fileId);

        GlobalSuccessResponseDto<Object> deleteFileAndDirectMessageSuccessResponse =
                globalService.successResponse(DELETE_FILE_AND_DIRECT_MESSAGE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(deleteFileAndDirectMessageSuccessResponse);
    }

    // TODO : test code
    @DeleteMapping("/file/{workSpaceId}/chat")
    public ResponseEntity<Object> deleteFileAndChatMessage(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                             @RequestParam(value = "fileId") Long fileId) {
        log.info("채팅 파일 & 메세지 삭제 API");
        fileAndMessageDeleteService.deleteFileAndChatMessage(workSpaceId, fileId);

        GlobalSuccessResponseDto<Object> deleteFileAndChatMessageSuccessResponse =
                globalService.successResponse(DELETE_FILE_AND_CHAT_MESSAGE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(deleteFileAndChatMessageSuccessResponse);
    }

}
