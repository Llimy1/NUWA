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

import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_FILE_AND_MESSAGE_SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileAndMessageController {

    private final FileAndMessageDeleteService fileAndMessageDeleteService;
    private final GlobalService globalService;

    // TODO : test code
    // TODO: 수정 해야함
    @DeleteMapping("/file/{workSpaceId}/channel")
    public ResponseEntity<Object> deleteFileAndMessage(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                       @RequestParam(value = "fileId") Long fileId) {
        log.info("파일 & 메세지 삭제 API");
        fileAndMessageDeleteService.deleteFileAndDirectMessage(workSpaceId, fileId);

        GlobalSuccessResponseDto<Object> deleteFileAndMessageSuccessResponse =
                globalService.successResponse(DELETE_FILE_AND_MESSAGE_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(deleteFileAndMessageSuccessResponse);
    }
}
