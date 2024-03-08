package org.project.nuwabackend.api.channel;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.channel.request.ChatChannelJoinMemberRequestDto;
import org.project.nuwabackend.dto.channel.request.ChatChannelRequestDto;
import org.project.nuwabackend.dto.channel.response.ChatChannelListResponseDto;
import org.project.nuwabackend.dto.channel.response.ChatChannelRoomIdResponseDto;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.channel.ChatChannelRedisService;
import org.project.nuwabackend.service.channel.ChatChannelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.type.SuccessMessage.CHAT_CHANNEL_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.CREATE_CHAT_CHANNEL_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_CHAT_CHANNEL_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.JOIN_CHAT_CHANNEL_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatChannelController {

    private final ChatChannelService chatChannelService;
    private final ChatChannelRedisService chatChannelRedisService;
    private final GlobalService globalService;

    @PostMapping("/channel/chat")
    public ResponseEntity<Object> createChatChannel(@MemberEmail String email,
                                                    @RequestBody ChatChannelRequestDto chatChannelRequestDto) {
        log.info("채팅 채널 생성 API");
        String chatChannelRoomId = chatChannelService.createChatChannel(email, chatChannelRequestDto);

        ChatChannelRoomIdResponseDto chatChannelRoomIdResponseDto = new ChatChannelRoomIdResponseDto(chatChannelRoomId);
        GlobalSuccessResponseDto<Object> createChatChannelSuccessResponse =
                globalService.successResponse(
                        CREATE_CHAT_CHANNEL_SUCCESS.getMessage(),
                        chatChannelRoomIdResponseDto);

        return ResponseEntity.status(CREATED).body(createChatChannelSuccessResponse);
    }

    @PostMapping("/channel/chat/join")
    public ResponseEntity<Object> joinChatChannel(@RequestBody ChatChannelJoinMemberRequestDto chatChannelJoinMemberRequestDto) {
        log.info("채팅 채널 참여 API");
        chatChannelService.joinChatChannel(chatChannelJoinMemberRequestDto);
        GlobalSuccessResponseDto<Object> joinChatChannelSuccessResponse =
                globalService.successResponse(JOIN_CHAT_CHANNEL_SUCCESS.getMessage(), null);

        return ResponseEntity.status(CREATED).body(joinChatChannelSuccessResponse);
    }

    // 채팅방 이름 순으로 조회
    @GetMapping("/channel/chat/{workSpaceId}")
    public ResponseEntity<Object> chatChannelList(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                             @CustomPageable Pageable pageable) {
        log.info("채팅 채널 리스트");
        Slice<ChatChannelListResponseDto> chatChannelListResponseDto =
                chatChannelService.chatChannelList(workSpaceId, pageable);

        GlobalSuccessResponseDto<Object> chatChannelListReturnSuccess =
                globalService.successResponse(CHAT_CHANNEL_LIST_RETURN_SUCCESS.getMessage(), chatChannelListResponseDto);

        return ResponseEntity.status(OK).body(chatChannelListReturnSuccess);
    }

    // 채팅창 나가기 (Redis 정보 삭제)
    @PostMapping("/channel/chat/{chatChannelRoomId}")
    public ResponseEntity<Object> deleteChatChannelMemberInfo(
            @PathVariable(value = "chatChannelRoomId") String chatChannelRoomId,
            @MemberEmail String email) {
        log.info("채팅방 나가기(Redis 정보 삭제");
        chatChannelRedisService.deleteChannelMemberInfo(chatChannelRoomId, email);

        GlobalSuccessResponseDto<Object> deleteChatChannelMemberInfo =
                globalService.successResponse(DELETE_CHAT_CHANNEL_MEMBER_INFO_SUCCESS.getMessage(), null);

        return ResponseEntity.status(OK).body(deleteChatChannelMemberInfo);
    }
}
