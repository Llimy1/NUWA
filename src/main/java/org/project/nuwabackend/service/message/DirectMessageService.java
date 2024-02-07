package org.project.nuwabackend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.project.nuwabackend.domain.redis.DirectChannelRedis;
import org.project.nuwabackend.dto.message.request.DirectMessageRequestDto;
import org.project.nuwabackend.dto.message.response.DirectMessageResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.global.type.ErrorMessage;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.mongo.DirectMessageRepository;
import org.project.nuwabackend.repository.redis.DirectChannelRedisRepository;
import org.project.nuwabackend.service.auth.JwtUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final DirectChannelRedisRepository directChannelRedisRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    // 메세지 저장
    @Transactional
    public void saveDirectMessage(String accessToken, DirectMessageRequestDto directMessageRequestDto) {
        DirectMessage directMessage = createDirectMessage(accessToken, directMessageRequestDto);
        directMessageRepository.save(directMessage);
    }

    // 메세지 전체 전달
    public DirectMessageResponseDto directSendMessage(String accessToken, DirectMessageRequestDto directMessageRequestDto) {

        DirectMessage directMessage = createDirectMessage(accessToken, directMessageRequestDto);

        String roomId = directMessage.getRoomId();
        String sender = directMessage.getSender();
        String content = directMessage.getContent();
        LocalDateTime createdAt = directMessage.getCreatedAt();
        Boolean isRead = directMessage.getIsRead();

        Integer readCount = isRead ? 0 : 1;

        return DirectMessageResponseDto.builder()
                .roomId(roomId)
                .sender(sender)
                .content(content)
                .readCount(readCount)
                .createdAt(createdAt)
                .build();
    }

    // 저장된 메세지 가져오기 (Slice)
    // 날짜 별로 가장 최신 순으로
    public Slice<DirectMessageResponseDto> directMessagesSortByDate(String roomId, Pageable pageable) {
        log.debug("저장된 채팅 가져오기");
        return directMessageRepository.findDirectMessagesByRoomId(roomId, pageable)
                .map(d -> DirectMessageResponseDto.builder()
                        .roomId(d.getRoomId())
                        .sender(d.getSender())
                        .content(d.getContent())
                        .readCount(d.getIsRead() ? 0 : 1)
                        .createdAt(d.getCreatedAt())
                        .build());
    }

    // DirectMessage 변환
    public DirectMessage createDirectMessage(String accessToken, DirectMessageRequestDto directMessageRequestDto) {

        String parseEmail = getEmail(accessToken);

        Member senderMember = memberRepository.findByEmail(parseEmail)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EMAIL_NOT_FOUND_MEMBER));

        // 다이렉트 메세지는 이름이 오지 않을까 생각함
        // 워크스페이스에 저장된 이름으로(?)
        // 워크스페이스 멤버에서 이름으로 멤버를 찾는다
        // TODO: ReceiverMember 가져오기

        log.debug("채팅 저장");
        String roomId = directMessageRequestDto.roomId();
        String sender = directMessageRequestDto.sender();
        String receiver = directMessageRequestDto.receiver();
        String content = directMessageRequestDto.content();

        // TODO: 생성된 direct member에서 sender와 receiver로 방에 속해 있는지 확인

        // TODO: 읽음, 읽지 않음 로직 생성 후 추가 -> if (방에 접속 중) true else false
        return DirectMessage.createDirectMessage(
                roomId, sender, content, false);
    }

    // Redis에 입장 정보 저장
    public void saveJoinMemberRedis(String accessToken, DirectMessageRequestDto directMessageRequestDto) {
        DirectMessage directMessage = createDirectMessage(accessToken, directMessageRequestDto);
        String directRoomId = directMessage.getRoomId();
        String email = getEmail(accessToken);

        DirectChannelRedis directChannelRedis =
                DirectChannelRedis.createDirectChannelRedis(directRoomId, email);

        directChannelRedisRepository.save(directChannelRedis);
    }

    // Redis에 입장 정보 삭제
    public void saveJoinMemberRedis(String accessToken) {
        String email = jwtUtil.getEmail(accessToken);

        // TODO: 나중에 삭제 정확히 구현 (현재 임시 구현)
        DirectChannelRedis directChannelRedis = directChannelRedisRepository.findByEmail(email)
                .orElseThrow();

        directChannelRedisRepository.delete(directChannelRedis);
    }

    // email 가져오기
    private String getEmail(String accessToken) {
        return jwtUtil.getEmail(accessToken);
    }

    // TODO: 다이렉트 현재 방 인원 모두 왔는지 확인 (처음 방에 접속하고 메세지를 날리면
    //  메세지를 저장하고 남겨둬야 나중에 들어온 인원이 볼 수 있다) && 인원에 따라 읽음, 읽지 않음 처리
    // TODO: 채팅 읽지 않은 개수 && 마지막 채팅과 시간 -> 알림 기능 (아마 SSE 쓸 예정)
}
