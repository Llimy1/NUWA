package org.project.nuwabackend.api.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequestDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelInfoResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelListResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelResponseDto;
import org.project.nuwabackend.dto.channel.response.DirectChannelRoomIdResponseDto;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.channel.DirectChannelRedisService;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_CREATE_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_INFO_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_LIST_RETURN_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[API] Direct Channel Controller Test")
@ExtendWith(MockitoExtension.class)
class DirectChannelControllerTest {

    @Mock
    DirectChannelService directChannelService;
    @Mock
    DirectChannelRedisService directChannelRedisService;
    @Mock
    GlobalService globalService;

    @InjectMocks
    DirectChannelController directChannelController;


    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    DirectChannelRequestDto directChannelRequestDto;

    String directChannelRoomId = "directChannelRoomId";
    String accessToken = "accessToken";
    String roomId = "roomId";
    String name = "channelName";
    Long workSpaceId = 1L;
    Long createMemberId = 1L;
    Long joinMemberId = 2L;
    String createMemberName = "createMemberName";
    String joinMemberName = "joinMemberName";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(directChannelController)
                .setCustomArgumentResolvers(new CustomPageableHandlerMethodArgumentResolver())
                .build();
        Long workSpaceId = 1L;
        Long joinMemberId = 1L;

        directChannelRequestDto = new DirectChannelRequestDto(workSpaceId, joinMemberId);
    }

    @Test
    @DisplayName("[API] Create Direct Channel Success Test")
    void createDirectChannelTest() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(directChannelRequestDto);

        DirectChannelRoomIdResponseDto directChannelRoomIdResponseDto =
                new DirectChannelRoomIdResponseDto(directChannelRoomId);

        GlobalSuccessResponseDto<Object> createDirectChannelSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(DIRECT_CHANNEL_CREATE_SUCCESS.getMessage())
                        .data(directChannelRoomIdResponseDto)
                        .build();

        given(directChannelService.createDirectChannel(any(), any()))
                .willReturn(directChannelRoomId);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(createDirectChannelSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/channel/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(DIRECT_CHANNEL_CREATE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.directChannelRoomId")
                        .value(directChannelRoomId))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Direct Channel Slice Test")
    void directChannelSliceTest() throws Exception {
        //given

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        List<DirectChannelListResponseDto> directChannelListResponseDto = new ArrayList<>(List.of(DirectChannelListResponseDto.builder()
                .roomId(roomId)
                .name(name)
                .workSpaceId(workSpaceId)
                .createMemberId(createMemberId)
                .joinMemberId(joinMemberId)
                .createMemberName(createMemberName)
                .joinMemberName(joinMemberName)
                .build()));

        SliceImpl<DirectChannelListResponseDto> directChannelListResponses =
                new SliceImpl<>(directChannelListResponseDto, pageRequest, false);

        given(directChannelService.directChannelSlice(any(), any(), any()))
                .willReturn(directChannelListResponses);

        GlobalSuccessResponseDto<Object> directChannelListSuccessResponse = GlobalSuccessResponseDto.builder()
                .status(SUCCESS.getValue())
                .message(DIRECT_CHANNEL_LIST_RETURN_SUCCESS.getMessage())
                .data(directChannelListResponses)
                .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(directChannelListSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/channel/direct/{workSpaceId}", workSpaceId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(DIRECT_CHANNEL_LIST_RETURN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].roomId")
                        .value(roomId))
                .andExpect(jsonPath("$.data.content[0].name")
                        .value(name))
                .andExpect(jsonPath("$.data.content[0].workSpaceId")
                        .value(workSpaceId))
                .andExpect(jsonPath("$.data.content[0].createMemberId")
                        .value(createMemberId))
                .andExpect(jsonPath("$.data.content[0].joinMemberId")
                        .value(joinMemberId))
                .andExpect(jsonPath("$.data.content[0].createMemberName")
                        .value(createMemberName))
                .andExpect(jsonPath("$.data.content[0].joinMemberName")
                        .value(joinMemberName))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Direct Channel Slice Sort By Message CreateDate Success Test")
    void directChannelSliceSortByMessageCreateDateSuccessTest() throws Exception {
        //given
        Long unReadCount = 10L;
        String lastMessage = "lastMessage";
        LocalDateTime createdAt = LocalDateTime.now();

        DirectChannelResponseDto directChannelResponseDto =
                DirectChannelResponseDto.builder()
                        .roomId(roomId)
                        .name(name)
                        .workSpaceId(workSpaceId)
                        .createMemberId(createMemberId)
                        .joinMemberId(joinMemberId)
                        .createMemberName(createMemberName)
                        .joinMemberName(joinMemberName)
                        .unReadCount(unReadCount)
                        .build();

        directChannelResponseDto.setLastMessage(lastMessage);
        directChannelResponseDto.setMessageCreatedAt(createdAt);

        List<DirectChannelResponseDto> directChannelResponseDtoList =
                new ArrayList<>(List.of(directChannelResponseDto));

        PageRequest pageRequest = PageRequest.of(0 , 10 ,Sort.by("createdAt"));
        SliceImpl<DirectChannelResponseDto> directChannelResponseDtoSlice =
                new SliceImpl<>(directChannelResponseDtoList, pageRequest, false);

        given(directChannelService.directChannelSliceSortByMessageCreateDateDesc(any(), any(), any()))
                .willReturn(directChannelResponseDtoSlice);

        GlobalSuccessResponseDto<Object> directChannelListResponse = GlobalSuccessResponseDto.builder()
                .status(SUCCESS.getValue())
                .message(DIRECT_CHANNEL_LAST_MESSAGE_LIST_RETURN_SUCCESS.getMessage())
                .data(directChannelResponseDtoSlice)
                .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(directChannelListResponse);

        //when
        //then
        mvc.perform(get("/api/channel/direct/v2/{workSpaceId}", workSpaceId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Delete Direct Channel Member Info Success Test")
    void deleteDirectChannelMemberInfo() throws Exception {
        //given
        GlobalSuccessResponseDto<Object> createDirectChannelSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS.getMessage())
                        .data(null)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(createDirectChannelSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/channel/direct/{directChannelRoomId}", directChannelRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Direct Channel Info Test")
    void directChannelInfoTest() throws Exception {
        //given
        Long channelId = 1L;
        String channelName = "name";
        Long createMemberId = 1L;
        String createMemberName = "create";
        Long joinMemberId = 2L;
        String joinMemberName = "join";
        Boolean isCreateMemberDelete = false;
        Boolean isJoinMemberDelete = false;

        DirectChannelInfoResponseDto directChannelInfoResponseDto = DirectChannelInfoResponseDto.builder()
                .channelId(channelId)
                .channelName(channelName)
                .createMemberId(createMemberId)
                .createMemberName(createMemberName)
                .isCreateDelete(isCreateMemberDelete)
                .joinMemberId(joinMemberId)
                .joinMemberName(joinMemberName)
                .isJoinDelete(isJoinMemberDelete)
                .build();
        given(directChannelService.directChannelInfo(any(), anyString()))
                .willReturn(directChannelInfoResponseDto);

        GlobalSuccessResponseDto<Object> globalSuccessResponseDto =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(DIRECT_CHANNEL_INFO_RETURN_SUCCESS.getMessage())
                        .data(directChannelInfoResponseDto)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(globalSuccessResponseDto);

        //when
        //then
        mvc.perform(get("/api/channel/direct/info/{workSpaceId}", workSpaceId)
                .param("directChannelRoomId", roomId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}