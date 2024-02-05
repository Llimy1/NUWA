package org.project.nuwabackend.domain.mongo;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.type.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Direct Message Domain Test")
class DirectMessageTest {

    String roomId = "roomId";
    String sender = "sender";
    String receiver = "receiver";
    String content = "content";


    @Test
    @DisplayName("[Domain] Create Direct Message Test")
    void createDirectMessageTest() {
        //given
        //when
        DirectMessage directMessage = DirectMessage.createDirectMessage(roomId, sender, receiver, content, MediaType.TEXT);

        //then
        assertThat(directMessage.getRoomId()).isEqualTo(roomId);
        assertThat(directMessage.getSender()).isEqualTo(sender);
        assertThat(directMessage.getReceiver()).isEqualTo(receiver);
        assertThat(directMessage.getContent()).isEqualTo(content);
        assertThat(directMessage.getType()).isEqualTo(MediaType.TEXT.name());
    }
}