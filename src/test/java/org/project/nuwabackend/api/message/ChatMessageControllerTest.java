package org.project.nuwabackend.api.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.message.response.ChatMessageListResponseDto;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.message.ChatMessageService;
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
import static org.project.nuwabackend.global.type.SuccessMessage.CHAT_MESSAGE_LIST_RETURN_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[API] Chat Message Controller Test")
@ExtendWith(MockitoExtension.class)
class ChatMessageControllerTest {

    @Mock
    ChatMessageService chatMessageService;
    @Mock
    GlobalService globalService;

    @InjectMocks
    ChatMessageController chatMessageController;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(chatMessageController)
                .setCustomArgumentResolvers(new CustomPageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("[API] Chat Message Success Test")
    void chatMessageSuccessTest() throws Exception {
        //given
        Long workSpaceId = 1L;
        String roomId = "roomId";
        Long senderId = 1L;
        String senderName = "홍길동";
        String content = "안녕하세요";

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt"));

        List<ChatMessageListResponseDto> chatMessageListResponseDtoList =
                List.of(ChatMessageListResponseDto.builder()
                .workSpaceId(workSpaceId)
                .roomId(roomId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content)
                .createdAt(LocalDateTime.now()).build());

        SliceImpl<ChatMessageListResponseDto> chatMessageListResponseDtoSlice =
                new SliceImpl<>(chatMessageListResponseDtoList, pageRequest, false);

        GlobalSuccessResponseDto<Object> chatMessageSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(CHAT_MESSAGE_LIST_RETURN_SUCCESS.getMessage())
                        .data(chatMessageListResponseDtoSlice)
                        .build();

        given(chatMessageService.chatMessageSliceSortByDate(anyString(), any()))
                .willReturn(chatMessageListResponseDtoSlice);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(chatMessageSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/message/chat/{chatChannelRoomId}", roomId))
                .andExpect(status().isOk())
                .andDo(print());
    }

}