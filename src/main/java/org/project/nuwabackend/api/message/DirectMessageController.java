package org.project.nuwabackend.api.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.message.request.DirectMessageRequestDto;
import org.project.nuwabackend.dto.message.request.MessageDeleteRequestDto;
import org.project.nuwabackend.dto.message.request.MessageUpdateRequestDto;
import org.project.nuwabackend.dto.message.response.DirectMessageResponseDto;
import org.project.nuwabackend.dto.message.response.MessageDeleteResponseDto;
import org.project.nuwabackend.dto.message.response.MessageUpdateResponseDto;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_MESSAGE_LIST_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.OK;


@Slf4j
@RestController
@RequiredArgsConstructor
public class DirectMessageController {

    private final SimpMessagingTemplate template;
    private final DirectMessageService directMessageService;
    private final DirectMessageQueryService directMessageQueryService;

    private final GlobalService globalService;

    private static final String DIRECT_DESTINATION = "/sub/direct/";

    // 입장 메세지
    @MessageMapping("/direct/enter/{roomId}")
    public void directEnter(@Header("Authorization") String accessToken, @DestinationVariable(value = "roomId") String roomId) {
        DirectMessageResponseDto directMessageResponseDto = directMessageService.enterMessage(accessToken, roomId);
        template.convertAndSend(DIRECT_DESTINATION + roomId, directMessageResponseDto);
    }

    // 메세지 보낼 때
    @MessageMapping("/direct/send")
    public void directSend(@Header("Authorization") String accessToken, DirectMessageRequestDto directMessageRequestDto) {
        String roomId = directMessageRequestDto.roomId();
        DirectMessageResponseDto directMessageResponse =
                directMessageService.sendMessage(accessToken, directMessageRequestDto);

        DirectMessageResponseDto directMessageResponseDto = directMessageService.saveDirectMessage(directMessageResponse);
        template.convertAndSend(
                DIRECT_DESTINATION + roomId,
                directMessageResponseDto);
    }

    // 메세지 수정
    @MessageMapping("/direct/update")
    public void directUpdate(@Header("Authorization") String accessToken, MessageUpdateRequestDto messageUpdateRequestDto) {
        String roomId = messageUpdateRequestDto.roomId();
        MessageUpdateResponseDto messageUpdateResponseDto =
                directMessageQueryService.updateDirectMessage(accessToken, messageUpdateRequestDto);

        template.convertAndSend(
                DIRECT_DESTINATION + roomId,
                messageUpdateResponseDto);
    }

    // 메세지 삭제
    @MessageMapping("/direct/delete")
    public void directDelete(@Header("Authorization") String accessToken, MessageDeleteRequestDto messageDeleteRequestDto) {
        String roomId = messageDeleteRequestDto.roomId();
        MessageDeleteResponseDto messageDeleteResponseDto =
                directMessageQueryService.deleteDirectMessage(accessToken, messageDeleteRequestDto);

        template.convertAndSend(
                DIRECT_DESTINATION + roomId,
                messageDeleteResponseDto);
    }

    @PostMapping("/test/direct/message")
    public ResponseEntity<Object> testDirectSend(@RequestHeader("Authorization") String accessToken, @RequestBody DirectMessageRequestDto directMessageRequestDto) {
        DirectMessageResponseDto directMessageResponseDto =
                directMessageService.sendMessage(accessToken, directMessageRequestDto);
        DirectMessageResponseDto directMessageResponseDto1 = directMessageService.saveDirectMessage(directMessageResponseDto);
        return ResponseEntity.ok(directMessageResponseDto1);
    }

    // 채팅 메세지 리스트 반환
    @GetMapping("/api/message/direct/{directChannelRoomId}")
    public ResponseEntity<Object> directMessageSliceOrderByCreatedDate(
            @PathVariable("directChannelRoomId") String directChannelRoomId,
            @CustomPageable Pageable pageable) {

        log.info("채팅 메세지 리스트 반환 API 호출");
        Slice<DirectMessageResponseDto> directMessageResponseDtoList =
                directMessageService.directMessageSliceOrderByCreatedDate(directChannelRoomId, pageable);

        GlobalSuccessResponseDto<Object> directMessageSuccessResponse =
                globalService.successResponse(
                        DIRECT_MESSAGE_LIST_RETURN_SUCCESS.getMessage(),
                        directMessageResponseDtoList);

        return ResponseEntity.status(OK).body(directMessageSuccessResponse);
    }
}
