package org.project.nuwabackend.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Channel;
import org.project.nuwabackend.domain.notification.Notification;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.notification.request.NotificationIdListRequestDto;
import org.project.nuwabackend.dto.notification.response.NotificationGroupResponseDto;
import org.project.nuwabackend.dto.notification.response.NotificationListResponseDto;
import org.project.nuwabackend.dto.notification.response.NotificationResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChannelRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.notification.EmitterRepository;
import org.project.nuwabackend.repository.jpa.notification.NotificationRepository;
import org.project.nuwabackend.type.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.NOTIFICATION_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final ChannelRepository channelRepository;

    private final NotificationQueryService notificationQueryService;

    // 29분
    // TODO: 확인 필요
    private static final Long DEFAULT_TIME_OUT = 1000L * 60 * 29;

    private static final String DIRECT_PREFIX = "/direct-chat/";
    private static final String CHAT_PREFIX = "/groupChat/";

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
        saveEmitter.onCompletion(() -> {
            emitterRepository.deleteById(emitterId);
            emitterRepository.deleteAllStartWithId(String.valueOf(workSpaceMemberId));
            emitterRepository.deleteAllEventCacheStartWithId(String.valueOf(workSpaceMemberId));
        });
        // 타임아웃
        saveEmitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
            emitterRepository.deleteAllStartWithId(String.valueOf(workSpaceMemberId));
            emitterRepository.deleteAllEventCacheStartWithId(String.valueOf(workSpaceMemberId));
        });

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
    public void send(String content, String url, NotificationType notificationType, WorkSpaceMember sender, WorkSpaceMember receiver) {
        log.info("sse 알림 전송");
        Notification notification =
                Notification.createNotification(content, url, notificationType, sender, receiver);

        String receiverId = String.valueOf(receiver.getId());
        // 알림 저장
        Notification saveNotification = notificationRepository.save(notification);
        // 워크스페이스에 들어온 유저 SseEmitter 모두 가져오기
        Map<String, SseEmitter> emitterMap = emitterRepository.findAllStartWithById(receiverId);
        emitterMap.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장 (유실된 데이터 처리)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key,
                            NotificationResponseDto.builder()
                                    .workSpaceId(saveNotification.getReceiver().getWorkSpace().getId())
                                    .notificationId(saveNotification.getId())
                                    .notificationContent(saveNotification.getContent())
                                    .notificationUrl(saveNotification.getUrl())
                                    .notificationType(saveNotification.getType())
                                    .notificationSenderId(saveNotification.getSender().getId())
                                    .notificationSenderName(saveNotification.getSender().getName())
                                    .notificationReceiverId(saveNotification.getReceiver().getId())
                                    .notificationReceiverName(saveNotification.getReceiver().getName())
                                    .createdAt(saveNotification.getCreatedAt())
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

    // 알림 그룹화 최근 시간 순으로 전송
    public Slice<NotificationGroupResponseDto> notificationV2(String email, Long workSpaceId, Pageable pageable) {
        // 알림 리스트 가져오기
        List<NotificationListResponseDto> notificationListResponseDtoList =
                notificationQueryService.notificationV2(email, workSpaceId, false);

        // 그룹화 (최근 시간순으로 변경 후 -> senderId, type, senderName으로 그룹화)
        Map<String, List<NotificationListResponseDto>> groupedNotifications = notificationListResponseDtoList.stream()
                .sorted(Comparator.comparing(NotificationListResponseDto::createdAt).reversed())
                .collect(Collectors.groupingBy(
                        notification -> notification.notificationSenderId() + "_" + notification.notificationType() + "_" + notification.notificationSenderName(),
                        // 순서를 유지하기 위한 linked hash map
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 그룹화 된 값에서 정보 매핑 후 DTO로 반환
        List<NotificationGroupResponseDto> notificationGroupResponseDtoList = groupedNotifications.entrySet().stream().map(entry -> {
            List<NotificationListResponseDto> groupedList = entry.getValue();
            List<Long> notificationIdList =
                    groupedList.stream().map(NotificationListResponseDto::notificationId).toList();
            Long contentCount = (long) groupedList.size();
            String[] keyParts = entry.getKey().split("_");
            Long senderId = Long.parseLong(keyParts[0]);
            NotificationType notificationType = NotificationType.valueOf(keyParts[1]);
            String senderName = keyParts[2];
            String notificationUrl = groupedList.get(0).notificationUrl();
            LocalDateTime createdAt = groupedList.get(0).createdAt();

            return NotificationGroupResponseDto.builder()
                    .notificationIdList(notificationIdList)
                    .contentCount(contentCount)
                    .senderId(senderId)
                    .senderName(senderName)
                    .notificationUrl(notificationUrl)
                    .notificationType(notificationType)
                    .createdAt(createdAt)
                    .build();
        }).toList();


        return sliceDtoResponse(notificationGroupResponseDtoList, pageable);
    }

    // 워크스페이스 ID로 해당 알림 전체 삭제
    // TODO: integrated test code
    @Transactional
    public void deleteNotificationWorkSpaceId(Long workSpaceId) {
        notificationRepository.deleteByWorkSpaceId(workSpaceId);
    }

    // 알림 읽음으로 변경
    @Transactional
    public void updateReadNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_NOT_FOUND));

        notification.updateReadNotification();
    }

    // 알림 읽음으로 변경 v2
    @Transactional
    public void updateReadNotificationList(NotificationIdListRequestDto notificationIdListRequestDto) {
        List<Long> notificationIdList = notificationIdListRequestDto.notificationIdList();
        notificationRepository.updateIsReadByNotificationIdList(notificationIdList);
    }

    // 다이렉트 채팅방 접속 시 관련 알림 전부 읽음 처리
    @Transactional
    public void updateReadNotificationByDirectRoomId(String email, Long workSpaceId, String roomId) {
        String notificationUrl = directUrl(roomId);
        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));
        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        notificationRepository.updateIsReadByRoomId(notificationUrl, findWorkSpaceMemberId);
    }

    // 그룹채팅 접속 시 관련 알림 전부 읽음 처리
    @Transactional
    public void updateReadNotificationByChatRoomId(String email, Long workSpaceId, String roomId) {
        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));
        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();

        Channel findChannel = channelRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long channelId = findChannel.getId();
        String notificationUrl = chatUrl(roomId, channelId);

        notificationRepository.updateIsReadByRoomId(notificationUrl, findWorkSpaceMemberId);
    }

    // 알림 전체 삭제 -> 전체 읽음 처리
//    @Transactional
//    public void updateAllReadNotificationByReceiverId(String email, Long workSpaceId) {
//        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
//                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));
//        Long findWorkSpaceMemberId = findWorkSpaceMember.getId();
//
//
//    }

    // Slice(페이징)
    private Slice<NotificationGroupResponseDto> sliceDtoResponse(List<NotificationGroupResponseDto> notificationGroupResponseDtoList, Pageable pageable) {
        boolean hasNext = notificationGroupResponseDtoList.size() > pageable.getPageSize();
        List<NotificationGroupResponseDto> notificationContent = hasNext ? notificationGroupResponseDtoList.subList(0, pageable.getPageSize()) : notificationGroupResponseDtoList;

        return new SliceImpl<>(notificationContent, pageable, hasNext);
    }

    private String directUrl(String roomId) {
        return DIRECT_PREFIX + roomId;
    }

    private String chatUrl(String roomId, Long channelId) {
        return CHAT_PREFIX + roomId + "/" + channelId;
    }

}
