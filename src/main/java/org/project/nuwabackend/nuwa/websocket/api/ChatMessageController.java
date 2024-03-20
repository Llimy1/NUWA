package org.project.nuwabackend.nuwa.websocket.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.websocket.dto.request.ChatMessageRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.request.MessageDeleteRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.request.MessageUpdateRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.ChatMessageListResponseDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.ChatMessageResponseDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.MessageDeleteResponseDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.MessageUpdateResponseDto;
import org.project.nuwabackend.global.annotation.custom.CustomPageable;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.websocket.service.ChatMessageQueryService;
import org.project.nuwabackend.nuwa.websocket.service.ChatMessageService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.response.type.SuccessMessage.CHAT_MESSAGE_LIST_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.OK;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate template;
    private final ChatMessageService chatMessageService;
    private final ChatMessageQueryService chatMessageQueryService;

    private final GlobalService globalService;

    private static final String CHAT_DESTINATION = "/sub/chat/";

    // 메세지 보낼 때
    @MessageMapping("/chat/send")
    public void chatSend(@Header("Authorization") String accessToken, ChatMessageRequestDto chatMessageRequestDto) {
        String roomId = chatMessageRequestDto.roomId();
        ChatMessageResponseDto chatMessageResponse =
                chatMessageService.sendMessage(accessToken, chatMessageRequestDto);
        ChatMessageResponseDto chatMessageResponseDto =
                chatMessageService.saveChatMessage(chatMessageResponse);

        template.convertAndSend(
                CHAT_DESTINATION + roomId,
                chatMessageResponseDto);
    }

    // 메세지 수정
    @MessageMapping("/chat/update")
    public void chatUpdate(@Header("Authorization") String accessToken, MessageUpdateRequestDto messageUpdateRequestDto) {
        String roomId = messageUpdateRequestDto.roomId();
        MessageUpdateResponseDto messageUpdateResponseDto =
                chatMessageQueryService.updateChatMessage(accessToken, messageUpdateRequestDto);

        template.convertAndSend(
                CHAT_DESTINATION + roomId,
                messageUpdateResponseDto);
    }

    // 메세지 삭제
    @MessageMapping("/chat/delete")
    public void chatDelete(@Header("Authorization") String accessToken, MessageDeleteRequestDto messageDeleteRequestDto) {
        String roomId = messageDeleteRequestDto.roomId();
        MessageDeleteResponseDto messageDeleteResponseDto =
                chatMessageQueryService.deleteChatMessage(accessToken, messageDeleteRequestDto);

        template.convertAndSend(
                CHAT_DESTINATION + roomId,
                messageDeleteResponseDto);
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
