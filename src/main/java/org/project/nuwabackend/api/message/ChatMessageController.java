package org.project.nuwabackend.api.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.message.request.ChatMessageRequestDto;
import org.project.nuwabackend.dto.message.response.ChatMessageListResponseDto;
import org.project.nuwabackend.dto.message.response.ChatMessageResponseDto;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.message.ChatMessageService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.CHAT_MESSAGE_LIST_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.OK;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate template;
    private final ChatMessageService chatMessageService;
    private final GlobalService globalService;

    private static final String DIRECT_DESTINATION = "/sub/chat/";

    // 메세지 보낼 때
    @MessageMapping("/chat/send")
    public void directSend(@Header("Authorization") String accessToken, ChatMessageRequestDto chatMessageRequestDto) {
        String rooId = chatMessageRequestDto.roomId();
        ChatMessageResponseDto chatMessageResponseDto =
                chatMessageService.sendMessage(accessToken, chatMessageRequestDto);
        template.convertAndSend(
                DIRECT_DESTINATION + rooId,
                chatMessageResponseDto);

        chatMessageService.saveChatMessage(chatMessageResponseDto);
    }

    // 채팅 메세지 리스트 반환
    @GetMapping("/api/message/chat/{chatChannelRoomId}")
    public ResponseEntity<Object> chatMessageSliceSortByDate(
            @PathVariable("chatChannelRoomId") String chatChannelRoomId,
            @CustomPageable Pageable pageable) {

        log.info("채팅 메세지 리스트 반환 API 호출");
        Slice<ChatMessageListResponseDto> chatMessageListResponseDtoSlice =
                chatMessageService.chatMessageSliceSortByDate(chatChannelRoomId, pageable);

        GlobalSuccessResponseDto<Object> chatMessageSuccessResponse =
                globalService.successResponse(
                        CHAT_MESSAGE_LIST_RETURN_SUCCESS.getMessage(),
                        chatMessageListResponseDtoSlice);

        return ResponseEntity.status(OK).body(chatMessageSuccessResponse);
    }
}
