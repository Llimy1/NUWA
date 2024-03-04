package org.project.nuwabackend.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.file.response.FileInfoResponseDto;
import org.project.nuwabackend.dto.file.response.FileUploadResponseDto;
import org.project.nuwabackend.dto.file.response.FileUrlResponseDto;
import org.project.nuwabackend.dto.file.response.TopSevenFileInfoResponseDto;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.resolver.CustomPageableHandlerMethodArgumentResolver;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.s3.FileService;
import org.project.nuwabackend.type.FileType;
import org.project.nuwabackend.type.FileUploadType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.global.type.GlobalResponseStatus.SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.FILE_INFO_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.FILE_UPLOAD_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.FILE_URL_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.SEARCH_FILE_INFO_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.TOP_SEVEN_FILE_INFO_RETURN_SUCCESS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[API] File Controller Test")
@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    FileService fileService;
    @Mock
    GlobalService globalService;

    @InjectMocks
    FileController fileController;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(fileController)
                .setCustomArgumentResolvers(new CustomPageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("[API] Upload File Test")
    void uploadFileTest() throws Exception {
        //given
        Long fileId = 1L;

        MockMultipartFile multipartFile =
                new MockMultipartFile("fileList", "test.jpg", IMAGE_JPEG_VALUE, "test".getBytes());
        MockMultipartFile multipartJson =
                new MockMultipartFile("fileRequestDto", "", APPLICATION_JSON_VALUE, "{\"workSpaceId\" : 1, \"channelId\" : 1}".getBytes());

        List<FileUploadResponseDto> fileUploadResponseDtoList = List.of(FileUploadResponseDto.builder()
                .fileId(fileId)
                .fileUploadType(FileUploadType.IMAGE)
                        .fileType(FileType.CHAT)
                .build());

        given(fileService.upload(any(), any(), any(), any(), any()))
                .willReturn(fileUploadResponseDtoList);

        GlobalSuccessResponseDto<Object> uploadFileSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(FILE_UPLOAD_SUCCESS.getMessage())
                        .data(fileUploadResponseDtoList)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(uploadFileSuccessResponse);

        //when
        //then
        mvc.perform(multipart("/api/file/upload")
                        .file(multipartFile)
                        .file(multipartJson)
                .header("email", "abcd@gmail.com")
                        .param("fileType", "CHAT"))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Upload File Url List Test")
    void uploadFileUrlList() throws Exception {
        //given
        Long fileId = 1L;
        String url = "url";

        List<FileUrlResponseDto> fileUrlResponseDtoList = List.of(FileUrlResponseDto.builder()
                .fileId(fileId)
                .fileUrl(url)
                .fileUploadType(FileUploadType.IMAGE)
                        .fileType(FileType.CHAT)
                .fileCreatedAt(LocalDateTime.now())
                .build());

        given(fileService.fileUrlList(any()))
                .willReturn(fileUrlResponseDtoList);

        GlobalSuccessResponseDto<Object> uploadFileUrlListSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(FILE_URL_RETURN_SUCCESS.getMessage())
                        .data(fileUrlResponseDtoList)
                        .build();

        given(globalService.successResponse(anyString(), any()))
                .willReturn(uploadFileUrlListSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/file/upload")
                .param("fileIdList", "1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] File Or Image List Test")
    void fileOrImageListTest() throws Exception {
        //given
        Long fileId = 1L;
        String fileName = "fileName";
        Long fileSize = 1024L;
        String fileExtension = "jpg";
        Long fileMemberUploadId = 1L;
        String fileMemberUploadName = "memberName";

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt"));
        List<FileInfoResponseDto> fileList = List.of(FileInfoResponseDto.builder()
                .fileId(fileId)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileExtension(fileExtension)
                .fileUploadType(FileUploadType.IMAGE)
                        .fileType(FileType.CHAT)
                .fileMemberUploadId(fileMemberUploadId)
                .fileMemberUploadName(fileMemberUploadName)
                .createdAt(LocalDateTime.now())
                .build());

        SliceImpl<FileInfoResponseDto> fileInfoResponseDtoSlice =
                new SliceImpl<>(fileList, pageRequest, false);

        given(fileService.fileList(any(), anyString(), any(), any()))
                .willReturn(fileInfoResponseDtoSlice);

        GlobalSuccessResponseDto<Object> fileInfoSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(FILE_INFO_RETURN_SUCCESS.getMessage())
                        .data(fileInfoResponseDtoSlice)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(fileInfoSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/file/{workSpaceId}", 1L)
                        .param("fileExtension", "")
                        .param("fileType", ""))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Search File List Test")
    void searchFileListTest() throws Exception {
        //given
        Long fileId = 1L;
        String fileName = "fileName";
        Long fileSize = 1024L;
        String fileExtension = "jpg";
        Long fileMemberUploadId = 1L;
        String fileMemberUploadName = "memberName";

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt"));
        List<FileInfoResponseDto> fileList = List.of(FileInfoResponseDto.builder()
                .fileId(fileId)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileExtension(fileExtension)
                .fileUploadType(FileUploadType.IMAGE)
                        .fileType(FileType.CHAT)
                .fileMemberUploadId(fileMemberUploadId)
                .fileMemberUploadName(fileMemberUploadName)
                .createdAt(LocalDateTime.now())
                .build());

        SliceImpl<FileInfoResponseDto> fileInfoResponseDtoSlice =
                new SliceImpl<>(fileList, pageRequest, false);

        given(fileService.searchFileName(any(), anyString(), anyString(), any(), any()))
                .willReturn(fileInfoResponseDtoSlice);

        GlobalSuccessResponseDto<Object> fileInfoSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(SEARCH_FILE_INFO_RETURN_SUCCESS.getMessage())
                        .data(fileInfoResponseDtoSlice)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(fileInfoSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/file/search/{workSpaceId}", 1L)
                .param("fileName", "f")
                .param("fileExtension", "")
                .param("fileType", ""))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[API] Top Seven File Info Test")
    void topSevenFileInfoTest() throws Exception {
        //given
        Long fileId = 1L;
        String fileName = "fileName";
        Long fileSize = 1024L;
        String fileExtension = "jpg";

        List<TopSevenFileInfoResponseDto> topSevenFileInfoResponseDtoList = List.of(TopSevenFileInfoResponseDto.builder()
                .fileId(fileId)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileExtension(fileExtension)
                .build());

        given(fileService.topSevenFileOrderByCreatedAt(any()))
                .willReturn(topSevenFileInfoResponseDtoList);

        GlobalSuccessResponseDto<Object> fileInfoSuccessResponse =
                GlobalSuccessResponseDto.builder()
                        .status(SUCCESS.getValue())
                        .message(TOP_SEVEN_FILE_INFO_RETURN_SUCCESS.getMessage())
                        .data(topSevenFileInfoResponseDtoList)
                        .build();
        given(globalService.successResponse(anyString(), any()))
                .willReturn(fileInfoSuccessResponse);

        //when
        //then
        mvc.perform(get("/api/file/{workSpaceId}/topseven", 1L))
                .andExpect(status().isOk())
                .andDo(print());
    }
}