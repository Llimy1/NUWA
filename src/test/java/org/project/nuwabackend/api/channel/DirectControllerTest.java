package org.project.nuwabackend.api.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.channel.request.DirectChannelRequest;
import org.project.nuwabackend.dto.channel.response.DirectChannelRoomIdResponse;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.channel.DirectChannelRedisService;
import org.project.nuwabackend.service.channel.DirectChannelService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.DIRECT_CHANNEL_CREATE_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[API] Direct Channel Controller Test")
@ExtendWith(MockitoExtension.class)
class DirectControllerTest {

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

    DirectChannelRequest directChannelRequest;

    String directChannelRoomId = "directChannelRoomId";
    String email = "abcd@gmail.com";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(directChannelController).build();

        Long workSpaceId = 1L;
        Long joinMemberId = 1L;

        directChannelRequest = new DirectChannelRequest(workSpaceId, joinMemberId);
    }

    @Test
    @DisplayName("[API] Create Direct Channel Success Test")
    void createDirectChannelTest() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(directChannelRequest);


        DirectChannelRoomIdResponse directChannelRoomIdResponse =
                new DirectChannelRoomIdResponse(directChannelRoomId);

        GlobalSuccessResponseDto<Object> createDirectChannelSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(DIRECT_CHANNEL_CREATE_SUCCESS.getMessage())
                        .data(directChannelRoomIdResponse)
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
                        .header("MemberEmail", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(DELETE_DIRECT_CHANNEL_MEMBER_INFO_SUCCESS.getMessage()))
                .andDo(print());
    }
}