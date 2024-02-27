package org.project.nuwabackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.notification.response.NotificationListResponseDto;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.notification.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.project.nuwabackend.global.type.SuccessMessage.NOTIFICATION_LIST_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final GlobalService globalService;

    // SSE 연결
    @GetMapping(value = "/notification", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@RequestParam Long workSpaceId,
                                                @RequestParam String email,
                                           @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        SseEmitter emitter = notificationService.subscribe(email, workSpaceId, lastEventId);

        return ResponseEntity.status(OK).body(emitter);
    }

    // 알림 조회
    @GetMapping("/api/notification/{workSpaceId}")
    public ResponseEntity<Object> notificationList(@MemberEmail String email,
                                                   @PathVariable(value = "workSpaceId") Long worSpaceId,
                                                   @CustomPageable Pageable pageable) {
        log.info("알림 조회 API");
        Slice<NotificationListResponseDto> notificationListResponseDtoSlice =
                notificationService.notificationList(email, worSpaceId, pageable);

        GlobalSuccessResponseDto<Object> notificationListSuccessResponse =
                globalService.successResponse(NOTIFICATION_LIST_RETURN_SUCCESS.getMessage(),
                notificationListResponseDtoSlice);

        return ResponseEntity.status(OK).body(notificationListSuccessResponse);
    }
}
