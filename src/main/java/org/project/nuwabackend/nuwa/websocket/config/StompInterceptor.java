package org.project.nuwabackend.nuwa.websocket.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.exception.custom.JwtException;
import org.project.nuwabackend.nuwa.websocket.service.DirectMessageQueryService;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelRedisService;
import org.project.nuwabackend.nuwa.channel.service.DirectChannelRedisService;
import org.project.nuwabackend.nuwa.notification.service.NotificationService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.project.nuwabackend.global.response.type.ErrorMessage.JWT_EXPIRED;

@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 우선 순위를 높게 설정해서 SecurityFilter들 보다 앞서 실행되게 해준다.
@Slf4j
@RequiredArgsConstructor
@Component
public class StompInterceptor implements ChannelInterceptor {

    private final DirectChannelRedisService directChannelRedisService;
    private final DirectMessageQueryService directMessageQueryService;
    private final ChatChannelRedisService chatChannelRedisService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        try {
            String email = verifyToken(getAccessToken(accessor));
            handleMessage(Objects.requireNonNull(accessor.getCommand()), accessor, email);
        } catch (Exception e) {
            log.error("메세지 처리 중 예외 발생: {}", e.getMessage());
        }
        return message;
    }

    private void handleMessage(StompCommand command, StompHeaderAccessor accessor, String email) {
        try {
            switch (command) {
                case CONNECT -> connect(accessor, email);
                case SUBSCRIBE, SEND -> verifyToken(getAccessToken(accessor));
            }
        } catch (Exception e) {
            log.error("STOMP 명령 처리 중 예외 발생 = {}", e.getMessage());
        }
    }

    // 연결시 채널 타입으로 각 로직 수행
    private void connect(StompHeaderAccessor accessor, String email) {
        String channelType = getChannelType(accessor);
        switch (channelType) {
            case "direct":
                connectToDirectChannel(accessor, email);
                break;
            case "chat":
                connectToChatChannel(accessor, email);
                break;
            case "voice":
                connectToVoiceChannel(accessor, email);
                break;
        }
    }

    // 토큰 꺼내기
    private String getAccessToken(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }

    // 토큰 판별
    private String verifyToken(String accessToken) {
        if (!jwtUtil.verifyToken(accessToken)) {
            throw new JwtException(JWT_EXPIRED);
        }

        return jwtUtil.getEmail(accessToken);
    }

    // 다이렉트 메세지 채널 연결시
    private void connectToDirectChannel(StompHeaderAccessor accessor, String email) {
        String directChannelRoomId = getRoomId(accessor);
        Long workSpaceId = Long.parseLong(getWorkSpaceId(accessor));

        // 다이렉트 채널 입장 -> Redis 정보 저장
        directChannelRedisService.saveChannelMemberInfo(directChannelRoomId, email);

        // 다이렉트 메세지 전부 읽음 처리
        directMessageQueryService.updateReadCountZero(directChannelRoomId, email);

        // 해당 채팅방 알림 전부 읽음 처리
        notificationService.updateReadNotificationByDirectRoomId(email, workSpaceId, directChannelRoomId);

    }

    private void connectToChatChannel(StompHeaderAccessor accessor, String email) {
        String chatChannelRoomId = getRoomId(accessor);
        Long workSpaceId = Long.parseLong(getWorkSpaceId(accessor));

        // 채팅 채널 입장 -> Redis 정보 저장
        chatChannelRedisService.saveChannelMemberInfo(chatChannelRoomId, email);

        // 해당 채팅방 알림 전부 읽음 처리
        notificationService.updateReadNotificationByChatRoomId(email, workSpaceId, chatChannelRoomId);
    }

    private void connectToVoiceChannel(StompHeaderAccessor accessor, String email) {

    }

    private String getChannelType(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("channelType");
    }

    private String getRoomId(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("channelRoomId");
    }

    private String getWorkSpaceId(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("workSpaceId");
    }
}
