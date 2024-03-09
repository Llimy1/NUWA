package org.project.nuwabackend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Direct;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.message.request.MessageDeleteRequestDto;
import org.project.nuwabackend.dto.message.request.MessageUpdateRequestDto;
import org.project.nuwabackend.dto.message.response.MessageDeleteResponseDto;
import org.project.nuwabackend.dto.message.response.MessageUpdateResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.project.nuwabackend.type.MessageType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.type.MessageType.DELETE;
import static org.project.nuwabackend.type.MessageType.FILE;
import static org.project.nuwabackend.type.MessageType.UPDATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectMessageQueryService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectChannelRepository directChannelRepository;
    private final MongoTemplate mongoTemplate;
    private final JwtUtil jwtUtil;

    // 다이렉트 채널 읽지 않은 메세지 전부 읽음으로 변경 => 벌크 연산
    public void updateReadCountZero(String directChannelRoomId, String email) {
        log.info("채팅 전부 읽음으로 변경");

        Direct direct = directChannelRepository.findByRoomId(directChannelRoomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long workSpaceId = direct.getWorkSpace().getId();

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 보낸 사람이 아닌 메세지를 전부 읽음 처리
        Update update = new Update().set("direct_read_count", 0);
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").ne(senderId));
        mongoTemplate.updateMulti(query, update, DirectMessage.class);
    }

    // 읽지 않은 메세지 카운트
    public Long countUnReadMessage(String directChannelRoomId, String email, Long workSpaceId) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 접속한 멤버ID가 아닌 메세지 전부 카운트
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_read_count").is(1L)
                .and("direct_sender_id").ne(senderId));
        return mongoTemplate.count(query, DirectMessage.class);
    }

    // 내가 보낸 메세지 카운트
    public Long countManyMessageSenderId(String directChannelRoomId, String email, Long workSpaceId) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 해당 워크스페이스에 있는 메세지 중
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").is(senderId));
        return mongoTemplate.count(query, DirectMessage.class);
    }

    // 내가 아닌 상대방이 보낸 메세지로 상대방 id 찾아오기
    public Long neSenderId(String directChannelRoomId, String email, Long workSpaceId) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").ne(senderId));

        DirectMessage directMessage = mongoTemplate.findOne(query, DirectMessage.class);

//        if (directMessage != null) {
//            return directMessage.getSenderId();
//        } else {
//            throw new IllegalArgumentException(DIRECT_MESSAGE_NOT_FOUND.getMessage());
//        }
        return directMessage != null ? directMessage.getSenderId() : null;
    }

    // 메세지 수정
    // TODO: test code
    public MessageUpdateResponseDto updateMessage(String accessToken, MessageUpdateRequestDto messageUpdateRequestDto) {
        String email = jwtUtil.getEmail(accessToken);
        String id = messageUpdateRequestDto.id();
        String roomId = messageUpdateRequestDto.roomId();
        Long workSpaceId = messageUpdateRequestDto.workSpaceId();
        String content = messageUpdateRequestDto.content();
        MessageType messageType = messageUpdateRequestDto.messageType();
        boolean isEdited = messageType.equals(UPDATE);

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("id").is(id)
                .and("direct_room_id").is(roomId)
                .and("direct_sender_id").is(senderId));

        Update update = new Update().set("direct_content", content).set("is_edited", isEdited);
        mongoTemplate.updateMulti(query, update, DirectMessage.class);

        return MessageUpdateResponseDto.builder()
                .id(id)
                .roomId(roomId)
                .workSpaceId(workSpaceId)
                .content(content)
                .messageType(messageType)
                .isEdited(isEdited)
                .build();
    }

    // 메세지 삭제
    public MessageDeleteResponseDto deleteMessage(String accessToken, MessageDeleteRequestDto messageDeleteRequestDto) {
        String email = jwtUtil.getEmail(accessToken);
        String id = messageDeleteRequestDto.id();
        String roomId = messageDeleteRequestDto.roomId();
        Long workSpaceId = messageDeleteRequestDto.workSpaceId();
        MessageType messageType = messageDeleteRequestDto.messageType();
        boolean isDeleted = messageType.equals(DELETE);

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        String content = "삭제된 메세지입니다.";

        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("id").is(id)
                .and("direct_room_id").is(roomId)
                .and("direct_sender_id").is(senderId));

        Update update = new Update().set("direct_content", content).set("is_deleted", isDeleted);
        mongoTemplate.updateMulti(query, update, DirectMessage.class);

        return MessageDeleteResponseDto.builder()
                .id(id)
                .roomId(roomId)
                .workSpaceId(workSpaceId)
                .content(content)
                .isDeleted(isDeleted)
                .messageType(messageType)
                .build();
    }


    // 워크스페이스 ID로 관련 다이렉트 채팅 메세지 전부 삭제
    // TODO: test code
    public void deleteDirectMessageWorkSpaceId(Long workSpaceId) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId));

        mongoTemplate.remove(query, DirectMessage.class);
    }

    // 파일 URL과 MessageType이 File인거를 찾아서 삭제
    // TODO: test code
    public void deleteDirectMessageByFile(Long workSpaceId, String fileUrl) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("direct_content").is(fileUrl)
                .and("message_type").is(FILE));
        mongoTemplate.remove(query, DirectMessage.class);
    }

    // 워크스페이스 ID와 Room ID 관련 다이렉트 메세지 전부 삭제
    // TODO: test code
    public void deleteDirectMessageByWorkSpaceIdAndRoomId(Long workSpaceId, String roomId) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("direct_room_id").is(roomId));

        mongoTemplate.remove(query, DirectMessage.class);
    }
}
