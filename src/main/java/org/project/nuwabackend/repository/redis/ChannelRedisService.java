package org.project.nuwabackend.repository.redis;

public interface ChannelRedisService {

    // Redis에 채널 입장 정보 저장
    void saveChannelMemberInfo(String channelRoomId, String email);
    void deleteChannelMemberInfo(String channelRoomId, String email);
}
