package org.project.nuwabackend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.mongo.ChatMessage;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageQueryService {

    private final MongoTemplate mongoTemplate;

    // 워크스페이스 ID로 관련 채팅 채널 메세지 전부 삭제
    // TODO: test code
    public void deleteChatMessageWorkSpaceId(Long workSpaceId) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId));

        mongoTemplate.remove(query, ChatMessage.class);
    }
}
