package org.project.nuwabackend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectMessageQueryService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final MongoTemplate mongoTemplate;

    // 다이렉트 채널 읽지 않은 메세지 전부 읽음으로 변경 => 벌크 연산
    public void updateReadCountZero(String directChannelRoomId, String email) {
        log.info("채팅 전부 읽음으로 변경");

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 보낸 사람이 아닌 메세지를 전부 읽음 처리
        Update update = new Update().set("direct_read_count", 0);
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").ne(senderId));
        mongoTemplate.updateMulti(query, update, DirectMessage.class);
    }

    // 읽지 않은 메세지 카운트
    public Long countUnReadMessage(String directChannelRoomId, String email) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 접속한 멤버ID가 아닌 메세지 전부 카운트
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_read_count").is(1L)
                .and("direct_sender_id").is(senderId));
        return mongoTemplate.count(query, DirectMessage.class);
    }

    // 내가 보낸 메세지 카운트
    public Long countManyMessageSenderId(String directChannelRoomId, String email) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 해당 워크스페이스에 있는 메세지 중
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").is(senderId));
        return mongoTemplate.count(query, DirectMessage.class);
    }

    // 내가 아닌 상대방이 보낸 메세지로 상대방 id 찾아오기
    public Long neSenderId(String directChannelRoomId, String email) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").ne(senderId));

        DirectMessage directMessage = mongoTemplate.findOne(query, DirectMessage.class);

        // 찾은 메세지에서 상대방 사용자 ID 반환
        // TODO: 반환에 대해서 다시 고민
        return directMessage != null ? directMessage.getSenderId() : null;
    }
}
