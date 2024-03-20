package org.project.nuwabackend.nuwa.websocket.repository;

import org.project.nuwabackend.nuwa.domain.mongo.DirectMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DirectMessageRepository extends MongoRepository<DirectMessage, String> {

    Slice<DirectMessage> findDirectMessageByRoomIdOrderByCreatedAtDesc(String roomId, Pageable pageable);
}
