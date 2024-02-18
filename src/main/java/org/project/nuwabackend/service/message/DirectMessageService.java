package org.project.nuwabackend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.dto.message.request.DirectMessageRequestDto;
import org.project.nuwabackend.dto.message.response.DirectMessageResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.mongo.DirectMessageRepository;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

import static org.project.nuwabackend.global.type.ErrorMessage.EMAIL_NOT_FOUND_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final DirectChannelService directChannelService;
    private final MemberRepository memberRepository;
    private final MongoTemplate mongoTemplate;
    private final JwtUtil jwtUtil;

    // 메세지 저장
    @Transactional
    public void saveDirectMessage(DirectMessageResponseDto DirectMessageResponseDto) {
        String directChannelRoomId = DirectMessageResponseDto.roomId();
        Long directSenderId = DirectMessageResponseDto.senderId();
        String senderName = DirectMessageResponseDto.senderName();
        String directContent = DirectMessageResponseDto.content();
        Long readCount = DirectMessageResponseDto.readCount();

        DirectMessage directMessage = DirectMessage.createDirectMessage(
                directChannelRoomId,
                directSenderId,
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
                        .roomId(directMessage.getRoomId())
                        .senderId(directMessage.getSenderId())
                        .content(directMessage.getContent())
                        .readCount(directMessage.getReadCount())
                        .build());
    }

    // 메세지 보내기
    public DirectMessageResponseDto sendMessage(String accessToken, DirectMessageRequestDto directMessageRequestDto) {

        String email = jwtUtil.getEmail(accessToken);
        String directChannelRoomId = directMessageRequestDto.roomId();
        String directChannelContent = directMessageRequestDto.content();

        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(EMAIL_NOT_FOUND_MEMBER));

        Long senderId = findMember.getId();

        boolean isAllConnected = directChannelService.isAllConnected(directChannelRoomId);

        Long readCount = isAllConnected ? 0L : 1L;

        return DirectMessageResponseDto.builder()
                .roomId(directChannelRoomId)
                .senderId(senderId)
                .content(directChannelContent)
                .readCount(readCount)
                .createdAt(LocalDateTime.now())
                .build();
    }


    // 다이렉트 채널 읽지 않은 메세지 전부 읽음으로 변경 => 벌크 연산
    public void updateReadCountZero(String directChannelRoomId, String email) {
        log.info("채팅 전부 읽음으로 변경");

        Member sender = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(EMAIL_NOT_FOUND_MEMBER));

        Long senderId = sender.getId();

        // 보낸 사람이 아닌 메세지를 전부 읽음 처리
        Update update = new Update().set("direct_read_count", 0);
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").ne(senderId));
        mongoTemplate.updateMulti(query, update, DirectMessage.class);
    }
}



