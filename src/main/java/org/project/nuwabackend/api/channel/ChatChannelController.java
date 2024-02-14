package org.project.nuwabackend.api.channel;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.channel.request.ChatChannelJoinMemberRequest;
import org.project.nuwabackend.dto.channel.request.ChatChannelRequest;
import org.project.nuwabackend.dto.channel.response.ChatChannelIdResponse;
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
public class ChatChannelController {

    private final ChatChannelService chatChannelService;
    private final GlobalService globalService;

    // TODO: test code
    @PostMapping("/channel/chat")
    public ResponseEntity<Object> createChatChannel(@MemberEmail String email,
                                                    @RequestBody ChatChannelRequest chatChannelRequest) {
        log.info("채팅 채널 생성 API");
        Long chatChannelId = chatChannelService.createChatChannel(email, chatChannelRequest);

        ChatChannelIdResponse chatChannelIdResponse = new ChatChannelIdResponse(chatChannelId);
        GlobalSuccessResponseDto<Object> createChatChannelSuccessResponse =
                globalService.successResponse(
                        CREATE_CHAT_CHANNEL_SUCCESS.getMessage(),
                        chatChannelIdResponse);

        return ResponseEntity.status(CREATED).body(createChatChannelSuccessResponse);
    }

    // TODO: test code
    @PostMapping("/channel/chat/join")
    public ResponseEntity<Object> joinChatChannel(@RequestBody ChatChannelJoinMemberRequest chatChannelJoinMemberRequest) {
        log.info("채팅 채널 참여 API");
        chatChannelService.joinChatChannel(chatChannelJoinMemberRequest);
        GlobalSuccessResponseDto<Object> joinChatChannelSuccessResponse =
                globalService.successResponse(JOIN_CHAT_CHANNEL_SUCCESS.getMessage(), null);

        return ResponseEntity.status(CREATED).body(joinChatChannelSuccessResponse);
    }
}
