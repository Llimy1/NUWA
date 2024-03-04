package org.project.nuwabackend.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.notification.response.NotificationListResponseDto;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.global.type.GlobalResponseStatus;
import org.project.nuwabackend.global.type.SuccessMessage;
import org.project.nuwabackend.service.notification.NotificationService;
import org.project.nuwabackend.type.NotificationType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.NOTIFICATION_LIST_RETURN_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[API] Notification Controller Test")
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    NotificationService notificationService;
    @Mock
    GlobalService globalService;

    @InjectMocks
    NotificationController notificationController;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new CustomPageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("[SSE] SSE Subscribe Test")
    void sseSubscribeTest() throws Exception {
        //given
        SseEmitter sseEmitter = new SseEmitter();

        given(notificationService.subscribe(any(), any(), any()))
                .willReturn(sseEmitter);

        //when
        //then
        mvc.perform(get("/notification")
                .param("workSpaceId", "1")
                .param("email", "abcd@gmail.com")
                .param("Last-Event-ID", "")
                        .contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Notification List Test")
    void notificationListTest() throws Exception {
        //given
        Long notificationId = 1L;
        String content = "content";
        String url = "url";
        Boolean isRead = false;

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt"));

        List<NotificationListResponseDto> notificationListResponseDtoList = List.of(NotificationListResponseDto.builder()
                .notificationId(notificationId)
                .notificationContent(content)
                .notificationUrl(url)
                .isRead(isRead)
                .notificationType(NotificationType.DIRECT)
                .createdAt(LocalDateTime.now())
                .build());

        SliceImpl<NotificationListResponseDto> notificationListResponseDtos =
                new SliceImpl<>(notificationListResponseDtoList, pageRequest, false);

        given(notificationService.notificationList(any(), any(), any()))
                .willReturn(notificationListResponseDtos);

        GlobalSuccessResponseDto<Object> notificationListSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(NOTIFICATION_LIST_RETURN_SUCCESS.getMessage())
                        .data(notificationListResponseDtos)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(notificationListSuccessResponse);
        //when
        //then
        mvc.perform(get("/api/notification/{workSpaceId}", 1L)
                .param("email", "abcd@gmail.com"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}