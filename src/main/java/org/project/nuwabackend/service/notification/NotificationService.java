package org.project.nuwabackend.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.notification.Notification;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.notification.response.NotificationListResponseDto;
import org.project.nuwabackend.dto.notification.response.NotificationResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.notification.EmitterRepository;
import org.project.nuwabackend.repository.jpa.notification.NotificationRepository;
import org.project.nuwabackend.type.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    private final NotificationQueryService notificationQueryService;

    // 29분
    // TODO: 확인 필요
    private static final Long DEFAULT_TIME_OUT = 1000L * 60 * 29;

    // SSE 연결
    @Transactional
    public SseEmitter subscribe(String email, Long workSpaceId, String lastEventId) {

        // 워크스페이스 멤버 찾기
        WorkSpaceMember workSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long workSpaceMemberId = workSpaceMember.getId();

        // Emitter Id
        String emitterId = workSpaceMemberId + "_" + System.currentTimeMillis();

        // Emitter Id와 29분의 타임아웃을 가진 emitter를 생성 후 map에 저장
        SseEmitter saveEmitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIME_OUT));

        log.info("new Emitter = {}", saveEmitter);

        // 상황 별 emitter 삭제
        // 완료
        saveEmitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        // 타임아웃
        saveEmitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지한 더미 데이터 전송
        sendToClient(saveEmitter, emitterId, "Event Stream Created. [workSpaceMemberId =" + workSpaceMemberId +"]");

        // 클라이언트가 미수신한 Event 목록이 존재를 할 경우 전송하여 Event 유실 방지
        if (!lastEventId.isEmpty()) {
            Map<String, Object> eventList =
                    emitterRepository.findAllEventCacheStartWithId(String.valueOf(workSpaceMemberId));

            // 미수신한 Event 목록 전송
            eventList.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) > 0)
                    .forEach(entry -> sendToClient(saveEmitter, entry.getKey(), entry.getValue()));
        }
        return saveEmitter;
    }

    // 알림 전송
    // TODO: test code
    @Transactional
    public void send(String content, String url, NotificationType notificationType, WorkSpaceMember receiver) {
        log.info("sse 알림 전송");
        Notification notification =
                Notification.createNotification(content, url, notificationType, receiver);

        String workSpaceMemberId = String.valueOf(receiver.getId());
        // 알림 저장
        notificationRepository.save(notification);
        // 워크스페이스에 들어온 유저 SseEmitter 모두 가져오기
        Map<String, SseEmitter> emitterMap = emitterRepository.findAllStartWithById(workSpaceMemberId);
        emitterMap.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장 (유실된 데이터 처리)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key,
                            NotificationResponseDto.builder()
                                    .workSpaceId(notification.getReceiver().getWorkSpace().getId())
                                    .notificationId(notification.getId())
                                    .notificationContent(notification.getContent())
                                    .notificationUrl(notification.getUrl())
                                    .notificationType(notification.getType())
                                    .notificationWorkSpaceMemberId(notification.getReceiver().getId())
                                    .notificationWorkSpaceMemberName(notification.getReceiver().getName())
                                    .createdAt(notification.getCreatedAt())
                                    .build());
                }
        );
    }

    // 알림을 클라이언트에 전송
    private void sendToClient(SseEmitter sseEmitter, String emitterId, Object data) {
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

    // 알림을 최근 시간 순으로 전송
    public Slice<NotificationListResponseDto> notificationList(String email, Long workSpaceId, Pageable pageable) {
        return notificationQueryService.notificationListResponseDtoSlice(email, workSpaceId, false, pageable);
    }

    // 워크스페이스 ID로 해당 알림 전체 삭제
    public void deleteNotificationWorkSpaceId(Long workSpaceId) {
        notificationRepository.deleteByWorkSpaceId(workSpaceId);
    }
}
