package org.project.nuwabackend.nuwa.websocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.domain.channel.Chat;
import org.project.nuwabackend.nuwa.domain.mongo.ChatMessage;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.websocket.repository.ChatMessageRepository;
import org.project.nuwabackend.nuwa.websocket.dto.request.ChatMessageRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.ChatMessageListResponseDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.ChatMessageResponseDto;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.channel.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelRedisService;
import org.project.nuwabackend.nuwa.workspacemember.service.WorkSpaceMemberQueryService;
import org.project.nuwabackend.nuwa.notification.service.NotificationService;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;
import org.project.nuwabackend.nuwa.notification.type.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.project.nuwabackend.global.response.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.FILE;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.IMAGE;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.TEXT;

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

    private static final String PREFIX_URL = "/groupChat/";

    @Transactional
    public ChatMessageResponseDto sendMessage(String accessToken, ChatMessageRequestDto chatMessageRequestDto) {

        ChatMessageResponseDto chatMessageResponseDto;

        log.info("채팅 채널 메세지 보내기");
        try {
            Long workSpaceId = chatMessageRequestDto.workSpaceId();
            String roomId = chatMessageRequestDto.roomId();
            String content = chatMessageRequestDto.content();
            List<String> rawString = chatMessageRequestDto.rawString();
            MessageType messageType = chatMessageRequestDto.messageType();
            String notificationContent;

            if (messageType.equals(IMAGE)) {
                notificationContent = "사진";
            } else if (messageType.equals(FILE)) {
                notificationContent = "파일";
            } else if (messageType.equals(TEXT)) {
                notificationContent = rawString.get(0);
            } else {
                notificationContent = "";
            }

            String email = jwtUtil.getEmail(accessToken);

            WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                    .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

            Long senderId = findWorkSpaceMember.getId();
            String senderName = findWorkSpaceMember.getName();
            String senderImage = findWorkSpaceMember.getImage();

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
                        notificationContent,
                        createChatUrl(roomId),
                        NotificationType.CHAT,
                        findWorkSpaceMember,
                        chatMember
                );
            });

            chatMessageResponseDto = ChatMessageResponseDto.builder()
                    .workSpaceId(workSpaceId)
                    .roomId(roomId)
                    .senderId(senderId)
                    .senderName(senderName)
                    .senderImage(senderImage)
                    .content(content)
                    .rawString(rawString)
                    .messageType(messageType)
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("메세지 전송 중 오류 발생 = {}", e.getMessage());
            throw new IllegalStateException("메세지 전송 중 오류가 발생 했습니다. 오류 내용 = " + e.getMessage());
        }
        return chatMessageResponseDto;
    }

    // 채팅 저장
    public ChatMessageResponseDto saveChatMessage(ChatMessageResponseDto chatMessageResponseDto) {
        log.info("채팅 메세지 저장");
        Long workSpaceId = chatMessageResponseDto.getWorkSpaceId();
        String roomId = chatMessageResponseDto.getRoomId();
        Long senderId = chatMessageResponseDto.getSenderId();
        String senderName = chatMessageResponseDto.getSenderName();
        String senderImage = chatMessageResponseDto.getSenderImage();
        String content = chatMessageResponseDto.getContent();
        List<String> rawString = chatMessageResponseDto.getRawString();
        MessageType messageType = chatMessageResponseDto.getMessageType();
        LocalDateTime createdAt = chatMessageResponseDto.getCreatedAt();

        ChatMessage chatMessage =
                ChatMessage.createChatMessage(workSpaceId, roomId, senderId, senderName, senderImage, content, rawString, messageType, createdAt);

        ChatMessage saveChatMessage = chatMessageRepository.save(chatMessage);
        chatMessageResponseDto.setMessageId(saveChatMessage.getId());
        chatMessageResponseDto.setIsEdited(saveChatMessage.getIsEdited());
        chatMessageResponseDto.setIsDeleted(saveChatMessage.getIsDeleted());
        return chatMessageResponseDto;
    }

    // 저장된 메세지 가져오기 (Slice)
    // 날짜 별로 가장 최신 순으로
    public Slice<ChatMessageListResponseDto> chatMessageSliceSortByDate(String chatChannelRoomId, Pageable pageable) {
        log.info("채팅 메세지 조회");
        return chatMessageRepository.findChatMessageByRoomIdOrderByCreatedAtDesc(chatChannelRoomId, pageable)
                .map(chatMessage -> ChatMessageListResponseDto.builder()
                        .messageId(chatMessage.getId())
                        .workSpaceId(chatMessage.getWorkSpaceId())
                        .roomId(chatMessage.getRoomId())
                        .senderId(chatMessage.getSenderId())
                        .senderName(chatMessage.getSenderName())
                        .senderImage(chatMessage.getSenderImage())
                        .content(chatMessage.getContent())
                        .rawString(chatMessage.getRawString())
                        .isEdited(chatMessage.getIsEdited())
                        .isDeleted(chatMessage.getIsDeleted())
                        .messageType(chatMessage.getMessageType())
                        .createdAt(chatMessage.getCreatedAt())
                        .build());
    }

    private String createChatUrl(String chatChannelRoomId) {
        return PREFIX_URL + chatChannelRoomId;
    }
}
