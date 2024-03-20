package org.project.nuwabackend.domain.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.nuwa.domain.redis.ChatChannelRedis;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Chat Channel Test")
class ChatChannelRedisTest {

    @Test
    @DisplayName("[Domain] Create Chat Channel Test")
    void createChatChannelTest() {
        //given
        String chatRoomId = "roomId";
        String email = "abcd@gmail.com";

        //when
        ChatChannelRedis chatChannelRedis =
                ChatChannelRedis.createChatChannelRedis(chatRoomId, email);

        //then
        assertThat(chatChannelRedis.getChatRoomId())
                .isEqualTo(chatRoomId);
        assertThat(chatChannelRedis.getEmail())
                .isEqualTo(email);
    }

}