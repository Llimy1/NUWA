package org.project.nuwabackend.config.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.global.exception.JwtException;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.project.nuwabackend.service.channel.ChatChannelRedisService;
import org.project.nuwabackend.service.channel.DirectChannelRedisService;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.project.nuwabackend.service.message.DirectMessageQueryService;
import org.project.nuwabackend.service.message.DirectMessageService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.project.nuwabackend.global.type.ErrorMessage.JWT_EXPIRED;

@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 우선 순위를 높게 설정해서 SecurityFilter들 보다 앞서 실행되게 해준다.
@Slf4j
@RequiredArgsConstructor
@Component
public class StompInterceptor implements ChannelInterceptor {

    private final DirectChannelRedisService directChannelRedisService;
    private final DirectMessageQueryService directMessageQueryService;
    private final ChatChannelRedisService chatChannelRedisService;
    private final JwtUtil jwtUtil;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String email = verifyToken(getAccessToken(accessor));
        log.info("StompHeaderAccessor = {}", accessor);
        handleMessage(Objects.requireNonNull(accessor.getCommand()), accessor, email);
        return message;
    }

    private void handleMessage(StompCommand command, StompHeaderAccessor accessor, String email) {

        switch (command) {
            case CONNECT:
                connect(accessor, email);
                break;
            case SUBSCRIBE:
            case SEND:
                verifyToken(getAccessToken(accessor));
                break;
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
        log.info("getAccessToken = " + accessor.getFirstNativeHeader("Authorization"));
        return accessor.getFirstNativeHeader("Authorization");
    }

    // 토큰 판별
    private String verifyToken(String accessToken) {
        log.info("accessToken = " + accessToken);
        if (!jwtUtil.verifyToken(accessToken)) {
            throw new JwtException(JWT_EXPIRED);
        }

        return jwtUtil.getEmail(accessToken);
    }

    // 다이렉트 메세지 채널 연결시
    private void connectToDirectChannel(StompHeaderAccessor accessor, String email) {
        String directChannelRoomId = getRoomId(accessor);

        // 다이렉트 채널 입장 -> Redis 정보 저장
        directChannelRedisService.saveChannelMemberInfo(directChannelRoomId, email);

        // 다이렉트 메세지 전부 읽음 처리
        directMessageQueryService.updateReadCountZero(directChannelRoomId, email);
    }

    private void connectToChatChannel(StompHeaderAccessor accessor, String email) {
        String chatChannelRoomId = getRoomId(accessor);

        // 채팅 채널 입장 -> Redis 정보 저장
        chatChannelRedisService.saveChannelMemberInfo(chatChannelRoomId, email);
    }

    // TODO: 음성 채널 연결시
    private void connectToVoiceChannel(StompHeaderAccessor accessor, String email) {

    }

    private String getChannelType(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("channelType");
    }

    private String getRoomId(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("channelRoomId");
    }
}
