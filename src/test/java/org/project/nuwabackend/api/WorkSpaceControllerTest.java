package org.project.nuwabackend.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.workspace.api.WorkSpaceController;
import org.project.nuwabackend.nuwa.workspace.api.WorkSpaceInquiryController;
import org.project.nuwabackend.nuwa.workspace.service.WorkSpaceInquiryService;
import org.project.nuwabackend.nuwa.workspacemember.api.WorkSpaceMemberController;
import org.project.nuwabackend.nuwa.workspacemember.dto.request.WorkSpaceMemberRequestDto;
import org.project.nuwabackend.nuwa.workspacemember.dto.request.WorkSpaceMemberUpdateRequestDto;
import org.project.nuwabackend.nuwa.workspace.dto.request.WorkSpaceRequestDto;
import org.project.nuwabackend.nuwa.workspace.dto.request.WorkSpaceUpdateRequestDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.inquiry.FavoriteWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.inquiry.IndividualWorkSpaceMemberInfoResponseDto;
import org.project.nuwabackend.nuwa.workspace.dto.response.workspace.WorkSpaceIdResponse;
import org.project.nuwabackend.nuwa.workspacemember.dto.response.WorkSpaceMemberIdResponse;
import org.project.nuwabackend.global.response.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.response.service.GlobalService;
import org.project.nuwabackend.nuwa.workspace.service.WorkSpaceService;
import org.project.nuwabackend.nuwa.workspacemember.service.WorkSpaceMemberService;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.response.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.CREATE_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.FAVORITE_WORK_SPACE_MEMBER_LIST_RETURN_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.INDIVIDUAL_WORK_SPACE_MEMBER_INFO_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.JOIN_WORK_SPACE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_INFO_UPDATE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_MEMBER_INFO_UPDATE_SUCCESS;
import static org.project.nuwabackend.global.response.type.SuccessMessage.WORK_SPACE_USE_SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("[API] WorkSpace Controller Test")
@ExtendWith(MockitoExtension.class)
class WorkSpaceControllerTest {

    @Mock
    WorkSpaceService workSpaceService;
    @Mock
    WorkSpaceMemberService workSpaceMemberService;
    @Mock
    WorkSpaceInquiryService workSpaceInquiryService;

    @Mock
    GlobalService globalService;

    @InjectMocks
    WorkSpaceController workSpaceController;
    @InjectMocks
    WorkSpaceInquiryController workSpaceInquiryController;
    @InjectMocks
    WorkSpaceMemberController workSpaceMemberController;

    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    String workSpaceName = "workSpaceName";
    String workSpaceImage = "workSpaceImage";
    String workSpaceIntroduce = "workSpaceIntroduce";
    String workSpaceMemberName = "workSpaceMemberName";
    String workSpaceMemberJob = "workSpaceMemberJob";
    String workSpaceMemberImage = "workSpaceMemberImage";
    Long workSpaceId = 1L;

    private WorkSpaceRequestDto workSpaceRequestDto() {
        return new WorkSpaceRequestDto(
                workSpaceName, workSpaceImage,
                workSpaceIntroduce, workSpaceMemberName,
                workSpaceMemberJob, workSpaceMemberImage);
    }

    private WorkSpaceMemberRequestDto workSpaceMemberRequestDto() {
        return new WorkSpaceMemberRequestDto(workSpaceId, workSpaceMemberImage);
    }

    @BeforeEach
    void setup() {
       mvc = MockMvcBuilders.standaloneSetup(workSpaceController, workSpaceInquiryController, workSpaceMemberController).build();
    }

    @Test
    @DisplayName("[API] Create WorkSpace Test")
    void createWorkSpaceTest() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(workSpaceRequestDto());
        Long workSpaceId = 1L;
        WorkSpaceIdResponse workSpaceIdResponse = new WorkSpaceIdResponse(workSpaceId);

        GlobalSuccessResponseDto<Object> createWorkSpaceSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(CREATE_WORK_SPACE_SUCCESS.getMessage())
                        .data(workSpaceIdResponse)
                        .build();

        given(workSpaceService.createWorkSpace(any(), any()))
                .willReturn(workSpaceId);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(createWorkSpaceSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/workspace")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(CREATE_WORK_SPACE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.workSpaceId")
                        .value(workSpaceId))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Join WorkSpace Member Test")
    void joinWorkSpaceMemberTest() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(workSpaceMemberRequestDto());
        Long workSpaceMemberId = 1L;
        WorkSpaceMemberIdResponse workSpaceMemberIdResponse = new WorkSpaceMemberIdResponse(workSpaceMemberId);

        GlobalSuccessResponseDto<Object> joinWorkSpaceMemberSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(JOIN_WORK_SPACE_SUCCESS.getMessage())
                        .data(workSpaceMemberIdResponse)
                        .build();

        given(workSpaceMemberService.joinWorkSpaceMember(any(), any()))
                .willReturn(workSpaceMemberId);
        given(globalService.successResponse(anyString(), any()))
                .willReturn(joinWorkSpaceMemberSuccessResponse);

        //when
        //then
        mvc.perform(post("/api/workspace/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(JOIN_WORK_SPACE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.workSpaceMemberId")
                        .value(workSpaceMemberId))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] WorkSpace Name Use Test")
    void duplicateWorkSpaceName() throws Exception {
        //given
        GlobalSuccessResponseDto<Object> workSpaceUseSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(WORK_SPACE_USE_SUCCESS.getMessage())
                        .data(null)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(workSpaceUseSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/workspace/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("workSpaceName", workSpaceName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value(SUCCESS.getValue()))
                .andExpect(jsonPath("$.message")
                        .value(WORK_SPACE_USE_SUCCESS.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Individual WorkSpace Member Info Test")
    void individualWorkSpaceMemberInfoTest() throws Exception {
        //given
        Long id = 1L;
        String name = "홍길동";
        String job = "백엔드";
        String image = "N";
        String status = "rest";
        String email = "abcd@gmail.com";
        String phoneNumber = "01000000000";

        IndividualWorkSpaceMemberInfoResponseDto individualWorkSpaceMemberInfoResponseDto =
                IndividualWorkSpaceMemberInfoResponseDto.builder()
                        .id(id)
                        .name(name)
                        .job(job)
                        .image(image)
                        .status(status)
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .workSpaceMemberType(WorkSpaceMemberType.CREATED)
                        .build();

        given(workSpaceInquiryService.individualWorkSpaceMemberInfo(any(), any()))
                .willReturn(individualWorkSpaceMemberInfoResponseDto);

        GlobalSuccessResponseDto<Object> individualWorkSpaceMemberInfoSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(INDIVIDUAL_WORK_SPACE_MEMBER_INFO_SUCCESS.getMessage())
                        .data(individualWorkSpaceMemberInfoResponseDto)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(individualWorkSpaceMemberInfoSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/workspace/{workSpaceId}/member", 1L)
                .param("email", "abcd@gmail.com"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Update WorkSpace Test")
    void updateWorkSpaceTest() throws Exception {
        //given
        String updateName = "updateName";
        String updateImage = "updateImage";
        String email = "abcd@gmail.com";
        Long workSpaceId = 1L;

        WorkSpaceUpdateRequestDto workSpaceUpdateRequestDto =
                new WorkSpaceUpdateRequestDto(updateName, updateImage);

        String body = objectMapper.writeValueAsString(workSpaceUpdateRequestDto);

        GlobalSuccessResponseDto<Object> updateWorkSpaceSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(WORK_SPACE_INFO_UPDATE_SUCCESS.getMessage())
                        .data(null)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(updateWorkSpaceSuccessResponse);

        //when
        //then
        mvc.perform(patch("/api/workspace/{workSpaceId}", workSpaceId)
                .param("email", email)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Update WorkSpace Member Test")
    void updateWorkSpaceMemberTest() throws Exception {
        //given
        String updateName = "updateName";
        String updateJob = "updateJob";
        String updateImage = "updateImage";
        String email = "abcd@gmail.com";
        Long workSpaceId = 1L;

        WorkSpaceMemberUpdateRequestDto workSpaceMemberUpdateRequestDto =
                new WorkSpaceMemberUpdateRequestDto(updateName, updateJob, updateImage);

        String body = objectMapper.writeValueAsString(workSpaceMemberUpdateRequestDto);

        GlobalSuccessResponseDto<Object> updateWorkSpaceMemberSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(WORK_SPACE_MEMBER_INFO_UPDATE_SUCCESS.getMessage())
                        .data(null)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(updateWorkSpaceMemberSuccessResponse);

        //when
        //then
        mvc.perform(patch("/api/workspace/{workSpaceId}/member", workSpaceId)
                        .param("email", email)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Favorite WorkSpace Member List Test")
    void favoriteWorkSpaceMemberListTest() throws Exception {
        //given
        Long id = 1L;
        String name = "name";
        String job = "job";
        String image = "image";
        String email = "abcd@gmail.com";
        String phoneNumber = "01000000000";
        Long messageCount = 10L;

        FavoriteWorkSpaceMemberInfoResponseDto build = FavoriteWorkSpaceMemberInfoResponseDto.builder()
                .id(id)
                .name(name)
                .job(job)
                .image(image)
                .email(email)
                .phoneNumber(phoneNumber)
                .messageCount(messageCount)
                .workSpaceMemberType(WorkSpaceMemberType.CREATED)
                .build();

        List<FavoriteWorkSpaceMemberInfoResponseDto> favoriteWorkSpaceMemberInfoResponseDtoList =
                new ArrayList<>(List.of(build));

        given(workSpaceInquiryService.favoriteWorkSpaceMemberList(any(), any()))
                .willReturn(favoriteWorkSpaceMemberInfoResponseDtoList);

        GlobalSuccessResponseDto<Object> globalSuccessResponseDto =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(FAVORITE_WORK_SPACE_MEMBER_LIST_RETURN_SUCCESS.getMessage())
                        .data(favoriteWorkSpaceMemberInfoResponseDtoList)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(globalSuccessResponseDto);


        //when
        //then
        mvc.perform(get("/api/workspace/{workSpaceId}/favorite", workSpaceId)
                .param("MemberEmail", email))
                .andExpect(status().isOk())
                .andDo(print());
    }
}