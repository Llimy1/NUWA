package org.project.nuwabackend.domain.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("[Domain] Direct Channel Redis Test")
class DirectChannelRedisTest {

    String email = "email";
    String roomId = "roomId";

    @Test
    @DisplayName("[Domain] Create Direct Channel Redis Test")
    void createDirectChannelRedisTest() {
        //given
        //when
        DirectChannelRedis directChannelRedis =
                DirectChannelRedis.createDirectChannelRedis(roomId, email);

        //then
        assertThat(directChannelRedis.getEmail()).isEqualTo(email);
        assertThat(directChannelRedis.getDirectRoomId()).isEqualTo(roomId);

    }


}