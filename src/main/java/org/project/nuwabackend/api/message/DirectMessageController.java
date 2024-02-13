package org.project.nuwabackend.api.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.message.DirectMessageDto;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.message.DirectMessageService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_MESSAGE_LIST_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.OK;


@Slf4j
@RestController
@RequiredArgsConstructor
public class DirectMessageController {

    private final SimpMessagingTemplate template;
    private final DirectMessageService directMessageService;
    private final GlobalService globalService;

    private static final String DIRECT_DESTINATION = "/sub/direct";

    // 메세지 보낼 때
    @MessageMapping("/direct/send")
    public void directSend(@Header("Authorization") String accessToken, DirectMessageDto directMessageDto) {
        String rooId = directMessageDto.roomId();
        DirectMessageDto directMessageResponse =
                directMessageService.sendMessage(accessToken, directMessageDto);
        template.convertAndSend(
                DIRECT_DESTINATION + rooId,
                directMessageResponse);

        directMessageService.saveDirectMessage(directMessageResponse);
    }

    // 채팅 메세지 리스트 반환
    @GetMapping("/direct/{directChannelRoomId}")
    public ResponseEntity<Object> directMessageSliceSortByDate(
            @PathVariable("directChannelRoomId") String directChannelRoomId,
            @CustomPageable Pageable pageable) {

        log.info("채팅 메세지 리스트 반환 API 호출");
        Slice<DirectMessageDto> directMessageResponseDtoList =
                directMessageService.directMessageSliceSortByDate(directChannelRoomId, pageable);

        GlobalSuccessResponseDto<Object> directMessageSuccessResponse =
                globalService.successResponse(
                        DIRECT_MESSAGE_LIST_RETURN_SUCCESS.getMessage(),
                        directMessageResponseDtoList);

        return ResponseEntity.status(OK).body(directMessageSuccessResponse);
    }
}
