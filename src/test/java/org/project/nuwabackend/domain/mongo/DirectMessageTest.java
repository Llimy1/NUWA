package org.project.nuwabackend.domain.mongo;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.type.MessageType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Direct Message Domain Test")
class DirectMessageTest {

    Long workSpaceId = 1L;
    String roomId = "roomId";
    Long senderId = 1L;
    String senderName = "senderName";
    String content = "content";
    List<String> rawString = new ArrayList<>(List.of("string"));
    Long readCount = 1L;
    LocalDateTime now = LocalDateTime.now();


    @Test
    @DisplayName("[Domain] Create Direct Message Test")
    void createDirectMessageTest() {
        //given
        //when
        DirectMessage directMessage = DirectMessage.createDirectMessage(workSpaceId, roomId, senderId, senderName, content, rawString, readCount, MessageType.TEXT, now);

        //then
        assertThat(directMessage.getWorkSpaceId()).isEqualTo(workSpaceId);
        assertThat(directMessage.getRoomId()).isEqualTo(roomId);
        assertThat(directMessage.getSenderId()).isEqualTo(senderId);
        assertThat(directMessage.getSenderName()).isEqualTo(senderName);
        assertThat(directMessage.getContent()).isEqualTo(content);
        assertThat(directMessage.getReadCount()).isEqualTo(readCount);
    }
}