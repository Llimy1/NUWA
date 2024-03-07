package org.project.nuwabackend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.mongo.ChatMessage;
import org.project.nuwabackend.type.MessageType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static org.project.nuwabackend.type.MessageType.FILE;

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

    // 파일 URL과 MessageType이 File인거를 찾아서 삭제
    // TODO: test code
    public void deleteChatMessageByFile(Long workSpaceId, String fileUrl) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("chat_content").is(fileUrl)
                .and("message_type").is(FILE));

        mongoTemplate.remove(query, ChatMessage.class);
    }

    // WorkSpace ID와 Room ID에 해당되는 채팅 전부 삭제
    // TODO: test code
    public void deleteWorkSpaceIdAndRoomId(Long workSpaceId, String roomId) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("chat_room_id").is(roomId));

        mongoTemplate.remove(query, ChatMessage.class);
    }
}
