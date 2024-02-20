package org.project.nuwabackend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.message.request.DirectMessageRequestDto;
import org.project.nuwabackend.dto.message.response.DirectMessageResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.mongo.DirectMessageRepository;
import org.project.nuwabackend.service.NotificationService;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.project.nuwabackend.type.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectMessageRepository directMessageRepository;
    private final DirectChannelService directChannelService;
    private final NotificationService notificationService;
    private final MongoTemplate mongoTemplate;
    private final JwtUtil jwtUtil;

    private static final String PREFIX_URL = "http://localhost:3000/";

    // 메세지 저장
    @Transactional
    public void saveDirectMessage(DirectMessageResponseDto DirectMessageResponseDto) {
        Long workSpaceId = DirectMessageResponseDto.workSpaceId();
        String directChannelRoomId = DirectMessageResponseDto.roomId();
        String senderName = DirectMessageResponseDto.senderName();
        String directContent = DirectMessageResponseDto.content();
        Long readCount = DirectMessageResponseDto.readCount();

        DirectMessage directMessage = DirectMessage.createDirectMessage(
                workSpaceId,
                directChannelRoomId,
                senderName,
                directContent,
                readCount);

        directMessageRepository.save(directMessage);
    }

    // 저장된 메세지 가져오기 (Slice)
    // 날짜 별로 가장 최신 순으로
    public Slice<DirectMessageResponseDto> directMessageSliceSortByDate(String directChannelRoomId, Pageable pageable) {
        return directMessageRepository.findDirectMessagesByRoomId(directChannelRoomId, pageable)
                .map(directMessage -> DirectMessageResponseDto.builder()
                        .workSpaceId(directMessage.getWorkSpaceId())
                        .roomId(directMessage.getRoomId())
                        .content(directMessage.getContent())
                        .readCount(directMessage.getReadCount())
                        .createdAt(directMessage.getCreatedAt())
                        .build());
    }

    // 메세지 보내기
    public DirectMessageResponseDto sendMessage(String accessToken, DirectMessageRequestDto directMessageRequestDto) {

        log.info("메세지 보내기");
        String email = jwtUtil.getEmail(accessToken);
        Long workSpaceId = directMessageRequestDto.workSpaceId();
        String directChannelRoomId = directMessageRequestDto.roomId();
        String directChannelContent = directMessageRequestDto.content();
        String receiverName = directMessageRequestDto.receiverName();

        // 채널들이 워크스페이스 멤버로 엮여 있는데 이 부분이 멤버인게 데이터가 맞지 않아 변경
//        Member findMember = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException(EMAIL_NOT_FOUND_MEMBER));

//        Long senderId = findMember.getId();

        // 메세지 보낸 사람
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        String senderName = sender.getName();

        // 메세지 받는 사람 (알림용)
        WorkSpaceMember receiver = workSpaceMemberRepository.findByName(receiverName)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        boolean isAllConnected = directChannelService.isAllConnected(directChannelRoomId);

        Long readCount = isAllConnected ? 0L : 1L;

        log.info("알림 전송");
        // 채팅을 읽지 않았을 때 알림을 전송
        // 읽었을 땐 알림을 보내지 않습니다.
        if (readCount.equals(0L)) {
            notificationService.send(
                    directChannelContent,
                    createDirectUrl(directChannelRoomId),
                    NotificationType.DIRECT,
                    receiver);
        }

        return DirectMessageResponseDto.builder()
                .workSpaceId(workSpaceId)
                .roomId(directChannelRoomId)
                .senderName(senderName)
                .content(directChannelContent)
                .readCount(readCount)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 다이렉트 채널 읽지 않은 메세지 전부 읽음으로 변경 => 벌크 연산
    public void updateReadCountZero(String directChannelRoomId, String email) {
        log.info("채팅 전부 읽음으로 변경");


        // 이부분도 workSpaceMember로 변경
//        Member sender = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException(EMAIL_NOT_FOUND_MEMBER));
//
//        Long senderId = sender.getId();

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        String senderName = sender.getName();

        // 보낸 사람이 아닌 메세지를 전부 읽음 처리
        Update update = new Update().set("direct_read_count", 0);
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_name").ne(senderName));
        mongoTemplate.updateMulti(query, update, DirectMessage.class);
    }

    // 읽지 않은 메세지 카운트
    public Long countUnReadMessage(String directChannelRoomId, String email) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        String senderName = sender.getName();

        // 접속한 멤버이름이 아닌 메세지 전부 카운트
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_read_count").is(1L)
                .and("direct_sender_name").is(senderName));
        return mongoTemplate.count(query, DirectMessage.class);
    }

    // TODO: 프론트 주소 확인해서 url 생성 해야함
    private String createDirectUrl(String directChannelRoomId) {
        return PREFIX_URL + directChannelRoomId;
    }
}



