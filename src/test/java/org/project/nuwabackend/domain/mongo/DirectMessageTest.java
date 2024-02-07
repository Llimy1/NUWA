package org.project.nuwabackend.domain.mongo;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Direct Message Domain Test")
class DirectMessageTest {

    String roomId = "roomId";
    String sender = "sender";
    String content = "content";
    Boolean isRead = false;


    @Test
    @DisplayName("[Domain] Create Direct Message Test")
    void createDirectMessageTest() {
        //given
        //when
        DirectMessage directMessage = DirectMessage.createDirectMessage(roomId, sender, content, isRead);

        //then
        assertThat(directMessage.getRoomId()).isEqualTo(roomId);
        assertThat(directMessage.getSender()).isEqualTo(sender);
        assertThat(directMessage.getContent()).isEqualTo(content);
        assertThat(directMessage.getIsRead()).isEqualTo(isRead);
    }
}