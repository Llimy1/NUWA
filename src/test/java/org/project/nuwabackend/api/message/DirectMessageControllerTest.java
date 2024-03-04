package org.project.nuwabackend.api.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.message.response.DirectMessageResponseDto;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.message.DirectMessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_MESSAGE_LIST_RETURN_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[API] Direct Message Controller Test")
@ExtendWith(MockitoExtension.class)
class DirectMessageControllerTest {

    @Mock
    DirectMessageService directMessageService;
    @Mock
    GlobalService globalService;

    @InjectMocks
    DirectMessageController directMessageController;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(directMessageController)
                .setCustomArgumentResolvers(new CustomPageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("[API] Direct Message Success Test")
    void directMessageSuccessTest() throws Exception {
        //given
        Long workSpaceId = 1L;
        String roomId = "roomId";
        Long senderId = 1L;
        String senderName = "홍길동";
        String content = "안녕하세요";
        Long readCount = 10L;

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt"));

        List<DirectMessageResponseDto> directMessageResponseDtoList =
                List.of(DirectMessageResponseDto.builder()
                        .workSpaceId(workSpaceId)
                        .roomId(roomId)
                        .senderId(senderId)
                        .senderName(senderName)
                        .content(content)
                        .readCount(readCount)
                        .createdAt(LocalDateTime.now()).build());

        SliceImpl<DirectMessageResponseDto> directMessageResponseDtoSlice =
                new SliceImpl<>(directMessageResponseDtoList, pageRequest, false);

        GlobalSuccessResponseDto<Object> directMessageSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(DIRECT_MESSAGE_LIST_RETURN_SUCCESS.getMessage())
                        .data(directMessageResponseDtoSlice)
                        .build();

        given(directMessageService.directMessageSliceOrderByCreatedDate(anyString(), any()))
                .willReturn(directMessageResponseDtoSlice);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(directMessageSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/message/direct/{directChannelRoomId}", roomId))
                .andExpect(status().isOk())
                .andDo(print());
    }

}