package org.project.nuwabackend.service.channel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.domain.redis.ChatChannelRedis;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelRedisService;
import org.project.nuwabackend.nuwa.channel.repository.redis.ChatChannelRedisRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@DisplayName("[Service] Chat Channel Redis Service Test")
@ExtendWith(MockitoExtension.class)
class ChatChannelRedisServiceTest {

    @Mock
    ChatChannelRedisRepository chatChannelRedisRepository;

    @InjectMocks
    ChatChannelRedisService chatChannelRedisService;

    @Test
    @DisplayName("[Service] Chat Connect Email List Test")
    void chatConnectEmailList() {
        //given
        String roomId = "roomId";
        String email = "abcd@gmail.com";

        ChatChannelRedis chatChannelRedis =
                ChatChannelRedis.createChatChannelRedis(roomId, email);

        List<String> connectEmailList = new ArrayList<>();
        connectEmailList.add(email);

        List<ChatChannelRedis> chatChannelRedisList =
                new ArrayList<>(List.of(chatChannelRedis));

        given(chatChannelRedisRepository.findByChatRoomId(anyString()))
                .willReturn(chatChannelRedisList);

        //when
        List<String> connectSEmailList =
                chatChannelRedisService.chatConnectEmailList(roomId);

        //then
        assertThat(connectSEmailList.get(0))
                .isEqualTo(connectEmailList.get(0));
    }

}