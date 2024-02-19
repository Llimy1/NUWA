package org.project.nuwabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.notification.EmitterRepository;
import org.project.nuwabackend.repository.jpa.notification.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static org.project.nuwabackend.global.type.ErrorMessage.MEMBER_ID_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    // 29분
    private static final Long DEFAULT_TIME_OUT = 1000L * 60 * 29;

    // SSE 연결
    @Transactional
    public SseEmitter subscribe(String email, String lastEventId) {

        // 워크스페이스 멤버 찾기
        WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new NotFoundException(MEMBER_ID_NOT_FOUND));

        // 해당된 멤버
        Long memberId = workSpaceMember.getId();

        // Emitter Id
        String emitterId = memberId + "_" + System.currentTimeMillis();

        // Emitter Id와 29분의 타임아웃을 가진 emitter를 생성 후 map에 저장
        SseEmitter saveEmitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIME_OUT));

        log.info("new Emitter = {}", saveEmitter);

        // 상황 별 emitter 삭제
        // 완료
        saveEmitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        // 타임아웃
        saveEmitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지한 더미 데이터 전송
        dummyDateSend(saveEmitter, emitterId, "Event Stream Created. [memberId =" + memberId +"]");

        // 클라이언트가 미수신한 Event 목록이 존재를 할 경우 전송하여 Event 유실 방지
        if (!lastEventId.isEmpty()) {

            Map<String, Object> eventList =
                    emitterRepository.findAllEventCacheStartWithId(String.valueOf(memberId));

            // 미수신한 Event 목록 전송
            eventList.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) > 0)
                    .forEach(entry -> dummyDateSend(saveEmitter, entry.getKey(), entry.getValue()));
        }
        return saveEmitter;
    }

    // 더미 데이터 반환 (503 Service Unavailable 방지)
    private void dummyDateSend(SseEmitter sseEmitter, String emitterId, Object data) {
        try {
            sseEmitter.send(SseEmitter.event()
                            .id(emitterId)
                            .name("sse")
                            .data(data)
                            .build());
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
            log.error("SSE 연결 오류", e);
        }
    }
}
