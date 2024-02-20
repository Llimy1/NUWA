package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.redis.DirectChannelRedis;
import org.project.nuwabackend.repository.redis.DirectChannelRedisRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectChannelRedisService {

    private final DirectChannelRedisRepository directChannelRedisRepository;


    // 채팅방 인원이 2명인지 확인 => 다이렉트 메세지를 보냈을 때 바로 읽음 처리를 하기 위한 메소드
    public boolean isAllConnected(String directChannelRoomId) {
        List<DirectChannelRedis> connectList = directChannelRedisRepository.findByDirectRoomId(directChannelRoomId);
        return connectList.size() == 2;
    }

    // 채팅방 인원이 1명인지 확인 => 채팅방 연결시 현재 인원이 존재 하는지 확인을 위한 메소드
    public boolean isConnected(String directChannelRoomId) {
        List<DirectChannelRedis> connectList = directChannelRedisRepository.findByDirectRoomId(directChannelRoomId);
        return connectList.size() == 1;
    }
}
