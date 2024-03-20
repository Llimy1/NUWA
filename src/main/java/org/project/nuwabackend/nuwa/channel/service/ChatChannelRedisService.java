package org.project.nuwabackend.nuwa.channel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.domain.redis.ChatChannelRedis;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.channel.repository.redis.ChannelRedisService;
import org.project.nuwabackend.nuwa.channel.repository.redis.ChatChannelRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.global.response.type.ErrorMessage.REDIS_CHAT_CHANNEL_AND_EMAIL_NOT_FOUND_INFO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatChannelRedisService implements ChannelRedisService {

    private final ChatChannelRedisRepository chatChannelRedisRepository;

    // Redis에 채널 입장 정보 저장
    @Override
    @Transactional
    public void saveChannelMemberInfo(String channelRoomId, String email) {
        log.info("채팅 채널 입장 정보 저장");
        ChatChannelRedis chatChannelRedis =
                ChatChannelRedis.createChatChannelRedis(channelRoomId, email);

        chatChannelRedisRepository.save(chatChannelRedis);
    }

    // Redis에 채널 입장 정보 삭제
    @Override
    @Transactional
    public void deleteChannelMemberInfo(String channelRoomId, String email) {
        log.info("채팅 채널 입장 정보 삭제");
        ChatChannelRedis chatChannelRedis =
                chatChannelRedisRepository.findByChatRoomIdAndEmail(channelRoomId, email)
                        .orElseThrow(() -> new NotFoundException(REDIS_CHAT_CHANNEL_AND_EMAIL_NOT_FOUND_INFO));

        chatChannelRedisRepository.delete(chatChannelRedis);
    }

    // 현재 Redis에 입장한 채널 정보 가져오기 -> 거기서 이메일만 따로 가져오기
    public List<String> chatConnectEmailList(String chatChannelRoomId) {
        List<String> connectEmailList = new ArrayList<>();
        List<ChatChannelRedis> chatChannelList =
                chatChannelRedisRepository.findByChatRoomId(chatChannelRoomId);

        chatChannelList.forEach(
                        chatChannelRedis -> connectEmailList.add(chatChannelRedis.getEmail()));

        return connectEmailList;
    }
}
