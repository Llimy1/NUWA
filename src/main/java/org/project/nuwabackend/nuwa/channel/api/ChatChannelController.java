package org.project.nuwabackend.nuwa.channel.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelJoinMemberRequestDto;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelRequestDto;
import org.project.nuwabackend.nuwa.channel.dto.response.ChatChannelInfoResponseDto;
import org.project.nuwabackend.nuwa.channel.dto.response.ChatChannelListResponseDto;
import org.project.nuwabackend.nuwa.channel.dto.response.ChatChannelIdResponseDto;
import org.project.nuwabackend.global.annotation.custom.CustomPageable;
import org.project.nuwabackend.global.annotation.custom.MemberEmail;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelRedisService;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.project.nuwabackend.global.response.type.SuccessMessage.CHAT_CHANNEL_INFO_RETURN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.CHAT_CHANNEL_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.CREATE_CHAT_CHANNEL_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.DELETE_CHAT_CHANNEL_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.JOIN_CHAT_CHANNEL_SUCCESS;
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
        Long chatChannelId = chatChannelService.createChatChannel(email, chatChannelRequestDto);

        ChatChannelIdResponseDto chatChannelIdResponseDto = new ChatChannelIdResponseDto(chatChannelId);
        GlobalSuccessResponseDto<Object> createChatChannelSuccessResponse =
                globalService.successResponse(
                        CREATE_CHAT_CHANNEL_SUCCESS.getMessage(),
                        chatChannelIdResponseDto);

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

    // 채팅방 조회
    @GetMapping("/channel/chat/{workSpaceId}")
    public ResponseEntity<Object> chatChannelList(@MemberEmail String email,
                                                  @PathVariable(value = "workSpaceId") Long workSpaceId,
                                                  @CustomPageable Pageable pageable) {
        log.info("채팅 채널 리스트");
        Slice<ChatChannelListResponseDto> chatChannelListResponseDto =
                chatChannelService.chatChannelList(email, workSpaceId, pageable);

        GlobalSuccessResponseDto<Object> chatChannelListReturnSuccess =
                globalService.successResponse(CHAT_CHANNEL_LIST_RETURN_SUCCESS.getMessage(), chatChannelListResponseDto);

        return ResponseEntity.status(OK).body(chatChannelListReturnSuccess);
    }

    // RoomId 정보 조회
    @GetMapping("/channel/chat/info/{workSpaceId}")
    public ResponseEntity<Object> chatChannelInfo(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                  @RequestParam(value = "chatChannelRoomId") String roomId) {
        log.info("채팅 채널 정보 반환 API");
        ChatChannelInfoResponseDto chatChannelInfoResponseDto =
                chatChannelService.joinChatChannelInfo(workSpaceId, roomId);

        GlobalSuccessResponseDto<Object> chatChannelInfoReturnSuccess =
                globalService.successResponse(CHAT_CHANNEL_INFO_RETURN_SUCCESS.getMessage(), chatChannelInfoResponseDto);

        return ResponseEntity.status(OK).body(chatChannelInfoReturnSuccess);
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
