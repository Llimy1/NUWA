package org.project.nuwabackend.api.channel;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.channel.request.ChatChannelJoinMemberRequest;
import org.project.nuwabackend.dto.channel.request.ChatChannelRequestDto;
import org.project.nuwabackend.dto.channel.response.ChatChannelRoomIdResponse;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.channel.ChatChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_CHAT_CHANNEL_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.JOIN_CHAT_CHANNEL_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
// TODO: test code 추후 로직 작성 후
public class ChatChannelController {

    private final ChatChannelService chatChannelService;
    private final GlobalService globalService;

    @PostMapping("/channel/chat")
    public ResponseEntity<Object> createChatChannel(@MemberEmail String email,
                                                    @RequestBody ChatChannelRequestDto chatChannelRequestDto) {
        log.info("채팅 채널 생성 API");
        String chatChannelRoomId = chatChannelService.createChatChannel(email, chatChannelRequestDto);

        ChatChannelRoomIdResponse chatChannelRoomIdResponse = new ChatChannelRoomIdResponse(chatChannelRoomId);
        GlobalSuccessResponseDto<Object> createChatChannelSuccessResponse =
                globalService.successResponse(
                        CREATE_CHAT_CHANNEL_SUCCESS.getMessage(),
                        chatChannelRoomIdResponse);

        return ResponseEntity.status(CREATED).body(createChatChannelSuccessResponse);
    }

    @PostMapping("/channel/chat/join")
    public ResponseEntity<Object> joinChatChannel(@RequestBody ChatChannelJoinMemberRequest chatChannelJoinMemberRequest) {
        log.info("채팅 채널 참여 API");
        chatChannelService.joinChatChannel(chatChannelJoinMemberRequest);
        GlobalSuccessResponseDto<Object> joinChatChannelSuccessResponse =
                globalService.successResponse(JOIN_CHAT_CHANNEL_SUCCESS.getMessage(), null);

        return ResponseEntity.status(CREATED).body(joinChatChannelSuccessResponse);
    }
}
