package org.project.nuwabackend.nuwa.delete.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.delete.service.ChannelAndMessageDeleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelAndMessageDeleteController {

    private final ChannelAndMessageDeleteService channelAndMessageDeleteService;
    private final GlobalService globalService;

    @DeleteMapping("/channel/chat/{workSpaceId}/message")
    public ResponseEntity<Object> deleteChatChannelAndMessage(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                              @MemberEmail String email,
                                                              @RequestParam Long channelId) {
        log.info("채팅 채널 & 메세지 & 파일 전부 삭제 API 호출");
        String successMessage = channelAndMessageDeleteService.deleteChatChannelAndChatMessage(workSpaceId, email, channelId);

        GlobalSuccessResponseDto<Object> deleteChatChannelAndMessageSuccessResponse =
                globalService.successResponse(successMessage, null);

        return ResponseEntity.status(OK).body(deleteChatChannelAndMessageSuccessResponse);
    }

    @DeleteMapping("/channel/direct/{workSpaceId}/message")
    public ResponseEntity<Object> deleteDirectChannelAndMessage(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                                @MemberEmail String email,
                                                                @RequestParam String roomId) {
        log.info("다이렉트 채널 & 메세지 & 파일 조건부 전부 삭제 API 호출");
        String successMessage = channelAndMessageDeleteService.deleteDirectChannelAndDirectMessage(workSpaceId, email, roomId);

        GlobalSuccessResponseDto<Object> deleteDirectChannelAndMessageSuccessResponse =
                globalService.successResponse(successMessage, null);

        return ResponseEntity.status(OK).body(deleteDirectChannelAndMessageSuccessResponse);
    }
}
