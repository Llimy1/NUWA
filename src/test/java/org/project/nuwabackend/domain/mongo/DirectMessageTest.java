package org.project.nuwabackend.domain.mongo;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Direct Message Domain Test")
class DirectMessageTest {

    Long workSpaceId = 1L;
    String roomId = "roomId";
    Long senderId = 1L;
    String content = "content";
    Long readCount = 1L;


    @Test
    @DisplayName("[Domain] Create Direct Message Test")
    void createDirectMessageTest() {
        //given
        //when
        DirectMessage directMessage = DirectMessage.createDirectMessage(workSpaceId, roomId, senderId, content, readCount);

        //then
        assertThat(directMessage.getWorkSpaceId()).isEqualTo(workSpaceId);
        assertThat(directMessage.getRoomId()).isEqualTo(roomId);
        assertThat(directMessage.getSenderId()).isEqualTo(senderId);
        assertThat(directMessage.getContent()).isEqualTo(content);
        assertThat(directMessage.getReadCount()).isEqualTo(readCount);
    }
}