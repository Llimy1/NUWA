package org.project.nuwabackend.api.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelJoinMemberRequestDto;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelRequestDto;
import org.project.nuwabackend.nuwa.channel.dto.response.ChatChannelListResponseDto;
import org.project.nuwabackend.nuwa.channel.dto.response.ChatChannelRoomIdResponseDto;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.annotation.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.channel.api.ChatChannelController;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelRedisService;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.response.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.CHAT_CHANNEL_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.CREATE_CHAT_CHANNEL_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.JOIN_CHAT_CHANNEL_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[API] Chat Channel Controller Test")
@ExtendWith(MockitoExtension.class)
class ChatChannelControllerTest {

    @Mock
    ChatChannelService chatChannelService;
    @Mock
    ChatChannelRedisService chatChannelRedisService;
    @Mock
    GlobalService globalService;

    @InjectMocks
    ChatChannelController chatChannelController;

    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(chatChannelController)
                .setCustomArgumentResolvers(new CustomPageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("[API] Create Chat Channel Test")
    void createChatChannelTest() throws Exception {
        Long workSpaceId = 1L;
        String chatChannelName = "channel";

        ChatChannelRequestDto chatChannelRequestDto =
                new ChatChannelRequestDto(workSpaceId, chatChannelName);

        String body = objectMapper.writeValueAsString(chatChannelRequestDto);

        //given
        String chatChannelRoomId = "roomId";
        given(chatChannelService.createChatChannel(any(), any()))
                .willReturn(chatChannelRoomId);

        ChatChannelRoomIdResponseDto chatChannelRoomIdResponseDto = new ChatChannelRoomIdResponseDto(chatChannelRoomId);

        GlobalSuccessResponseDto<Object> globalSuccessResponseDto =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(CREATE_CHAT_CHANNEL_SUCCESS.getMessage())
                        .data(chatChannelRoomIdResponseDto)
                        .build();
       given(globalService.successResponse(anyString(), any()))
               .willReturn(globalSuccessResponseDto);
        //when
        //then
        mvc.perform(post("/api/channel/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .param("MemberEmail", "abcd@gmail.com"))
                        .andExpect(status().isCreated())
                        .andDo(print());
    }

    @Test
    @DisplayName("[API] Join Chat Channel Test")
    void joinChatChannelTest() throws Exception {
        //given
        Long chatChannelId = 1L;
        List<Long> joinMemberIdList = new ArrayList<>(List.of(1L));

        ChatChannelJoinMemberRequestDto chatChannelJoinMemberRequestDto =
                new ChatChannelJoinMemberRequestDto(chatChannelId, joinMemberIdList);

        String body = objectMapper.writeValueAsString(chatChannelJoinMemberRequestDto);

        GlobalSuccessResponseDto<Object> globalSuccessResponseDto =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(JOIN_CHAT_CHANNEL_SUCCESS.getMessage())
                        .data(null)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(globalSuccessResponseDto);

        //when
        //then
        mvc.perform(post("/api/channel/chat/join")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Chat  Channel List Test")
    void chatChannelListTest() throws Exception {
        //given
        Long workSpaceId = 1L;
        Long channelId = 1L;
        String roomId = "roomId";
        String name = "name";

        ChatChannelListResponseDto build = ChatChannelListResponseDto.builder()
                .workSpaceId(workSpaceId)
                .channelId(channelId)
                .roomId(roomId)
                .name(name)
                .build();

        List<ChatChannelListResponseDto> list = new ArrayList<>(List.of(build));
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt"));
        SliceImpl<ChatChannelListResponseDto> chatChannelListResponseDtos =
                new SliceImpl<>(list, pageRequest, false);
        given(chatChannelService.chatChannelList(any(), any(), any()))
                .willReturn(chatChannelListResponseDtos);

        GlobalSuccessResponseDto<Object> globalSuccessResponseDto =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(CHAT_CHANNEL_LIST_RETURN_SUCCESS.getMessage())
                        .data(chatChannelListResponseDtos)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(globalSuccessResponseDto);

        //when
        //then
        mvc.perform(get("/api/channel/chat/{workSpaceId}", workSpaceId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}