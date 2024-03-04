package org.project.nuwabackend.repository.mongo;

import org.project.nuwabackend.domain.mongo.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Slice<ChatMessage> findChatMessageByRoomIdOrderByCreatedAtDesc(String chatChannelRoomId, Pageable pageable);
}
