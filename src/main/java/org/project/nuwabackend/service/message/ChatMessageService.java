package org.project.nuwabackend.service.message;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Chat;
import org.project.nuwabackend.domain.channel.ChatJoinMember;
import org.project.nuwabackend.domain.mongo.ChatMessage;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.message.request.ChatMessageRequestDto;
import org.project.nuwabackend.dto.message.response.ChatMessageListResponseDto;
import org.project.nuwabackend.dto.message.response.ChatMessageResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.repository.jpa.ChatJoinMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.mongo.ChatMessageRepository;
import org.project.nuwabackend.service.channel.ChatChannelRedisService;
import org.project.nuwabackend.service.workspace.WorkSpaceMemberQueryService;
import org.project.nuwabackend.service.notification.NotificationService;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.project.nuwabackend.type.MessageType;
import org.project.nuwabackend.type.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final WorkSpaceMemberQueryService workSpaceMemberQueryService;
    private final ChatChannelRedisService chatChannelRedisService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    private static final String PREFIX_URL = "http://localhost:3000/";

    @Transactional
    public ChatMessageResponseDto sendMessage(String accessToken, ChatMessageRequestDto chatMessageRequestDto) {
        log.info("채팅 채널 메세지 보내기");
        Long workSpaceId = chatMessageRequestDto.workSpaceId();
        String roomId = chatMessageRequestDto.roomId();
        String content = chatMessageRequestDto.content();
        MessageType messageType = chatMessageRequestDto.messageType();

        String email = jwtUtil.getEmail(accessToken);

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = findWorkSpaceMember.getId();
        String senderName = findWorkSpaceMember.getName();

        // RoomId로 해당 채널 가져오기
        Chat findChat = chatChannelRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long channelId = findChat.getId();

        // channelId로 해당 채널 참가 인원 리스트 가져오기
        // 채팅방 ID로 Redis 입장 정보 가져오기
        List<String> connectEmailList =
                chatChannelRedisService.chatConnectEmailList(roomId);

        // 접속한 유저를 제외한 워크스페이스 멤버 리스트
        List<WorkSpaceMember> chatMemberList =
                workSpaceMemberQueryService.chatCreateMemberOrJoinMemberNotInEmailAndChannelId(connectEmailList, channelId);

        chatMemberList.forEach(chatMember -> {
            log.info("알림 전송");
            notificationService.send(
                    content,
                    createChatUrl(workSpaceId, roomId),
                    NotificationType.CHAT,
                    chatMember
                    );
        });

        return ChatMessageResponseDto.builder()
                .workSpaceId(workSpaceId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content)
                .messageType(messageType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 채팅 저장
    public void saveChatMessage(ChatMessageResponseDto chatMessageResponseDto) {
        log.info("채팅 메세지 저장");
        Long workSpaceId = chatMessageResponseDto.workSpaceId();
        String roomId = chatMessageResponseDto.roomId();
        Long senderId = chatMessageResponseDto.senderId();
        String senderName = chatMessageResponseDto.senderName();
        String content = chatMessageResponseDto.content();
        MessageType messageType = chatMessageResponseDto.messageType();
        LocalDateTime createdAt = chatMessageResponseDto.createdAt();

        ChatMessage chatMessage =
                ChatMessage.createChatMessage(workSpaceId, roomId, senderId, senderName, content, messageType, createdAt);

        chatMessageRepository.save(chatMessage);
    }

    // 저장된 메세지 가져오기 (Slice)
    // 날짜 별로 가장 최신 순으로
    public Slice<ChatMessageListResponseDto> chatMessageSliceSortByDate(String chatChannelRoomId, Pageable pageable) {
        log.info("채팅 메세지 조회");
        return chatMessageRepository.findChatMessageByRoomIdOrderByCreatedAtDesc(chatChannelRoomId, pageable)
                .map(chatMessage -> ChatMessageListResponseDto.builder()
                        .workSpaceId(chatMessage.getWorkSpaceId())
                        .roomId(chatMessage.getRoomId())
                        .senderId(chatMessage.getSenderId())
                        .senderName(chatMessage.getSenderName())
                        .content(chatMessage.getContent())
                        .messageType(chatMessage.getMessageType())
                        .createdAt(chatMessage.getCreatedAt())
                        .build());
    }

    private String createChatUrl(Long workSpaceId, String chatChannelRoomId) {
        return PREFIX_URL + workSpaceId + "/" + chatChannelRoomId;
    }
}
