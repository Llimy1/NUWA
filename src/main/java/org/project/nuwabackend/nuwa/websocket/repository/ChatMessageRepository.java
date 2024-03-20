package org.project.nuwabackend.nuwa.websocket.repository;

import org.project.nuwabackend.nuwa.domain.mongo.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Slice<ChatMessage> findChatMessageByRoomIdOrderByCreatedAtDesc(String chatChannelRoomId, Pageable pageable);
}
