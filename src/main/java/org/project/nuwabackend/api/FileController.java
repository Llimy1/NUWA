package org.project.nuwabackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.file.request.FileRequestDto;
import org.project.nuwabackend.dto.file.response.FileInfoResponseDto;
import org.project.nuwabackend.dto.file.response.FileUploadResponseDto;
import org.project.nuwabackend.dto.file.response.FileUrlResponseDto;
import org.project.nuwabackend.dto.file.response.TopSevenFileInfoResponseDto;
import org.project.nuwabackend.global.annotation.CustomPageable;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.s3.FileService;
import org.project.nuwabackend.type.FileType;
import org.project.nuwabackend.type.FileUploadType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.project.nuwabackend.global.type.SuccessMessage.FILE_INFO_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.FILE_UPLOAD_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.FILE_URL_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.SEARCH_FILE_INFO_RETURN_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.TOP_SEVEN_FILE_INFO_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final GlobalService globalService;

    @PostMapping("/file/upload")
    public ResponseEntity<Object> uploadFile(@MemberEmail String email,
                                              @RequestParam(value = "fileType") FileType fileType,
                                              @RequestParam(value = "channelId", required = false) Long channelId,
                                              @RequestPart(name = "fileList") List<MultipartFile> multipartFileList,
                                              @RequestPart(name = "fileRequestDto") FileRequestDto fileRequestDto) {
        log.info("파일 업로드 API 호출");
        List<FileUploadResponseDto> uploadIdResponse = fileService.upload(email, fileType, channelId, multipartFileList, fileRequestDto);

        GlobalSuccessResponseDto<Object> uploadFileSuccessResponse =
                globalService.successResponse(FILE_UPLOAD_SUCCESS.getMessage(), uploadIdResponse);

        return ResponseEntity.status(CREATED).body(uploadFileSuccessResponse);
    }

    @GetMapping("/file/upload")
    public ResponseEntity<Object> uploadFileUrlList(@RequestParam(required = false) List<Long> fileIdList) {
        log.info("직전에 업로드한 파일 URL 조회 API 호출");
        List<FileUrlResponseDto> fileUrlListResponse = fileService.fileUrlList(fileIdList);
        GlobalSuccessResponseDto<Object> uploadFileUrlListSuccessResponse =
                globalService.successResponse(FILE_URL_RETURN_SUCCESS.getMessage(), fileUrlListResponse);

        return ResponseEntity.status(OK).body(uploadFileUrlListSuccessResponse);
    }

    @GetMapping("/file/{workSpaceId}")
    public ResponseEntity<Object> fileOrImageList(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                  @RequestParam(value = "fileExtension", required = false) String fileExtension,
                                                  @RequestParam(value = "fileUploadType", required = false) FileUploadType fileUploadType,
                                                  @CustomPageable Pageable pageable) {
        log.info("파일 조회 API 호출");
        Slice<FileInfoResponseDto> fileInfoResponseDtoList =
                fileService.fileList(workSpaceId, fileExtension, fileUploadType, pageable);

        GlobalSuccessResponseDto<Object> fileInfoSuccessResponse =
                globalService.successResponse(FILE_INFO_RETURN_SUCCESS.getMessage(),
                fileInfoResponseDtoList);

        return ResponseEntity.status(OK).body(fileInfoSuccessResponse);
    }

    @GetMapping("/file/search/{workSpaceId}")
    public ResponseEntity<Object> searchFileList(@PathVariable(value = "workSpaceId") Long workSpaceId,
                                                  @RequestParam(value = "fileName", required = false) String fileName,
                                                  @RequestParam(value = "fileExtension", required = false) String fileExtension,
                                                  @RequestParam(value = "fileUploadType", required = false) FileUploadType fileUploadType,
                                                  @CustomPageable Pageable pageable) {
        log.info("파일 검색 API 호출");
        Slice<FileInfoResponseDto> searchFileInfoResponseDtoList =
                fileService.searchFileName(workSpaceId, fileName, fileExtension, fileUploadType, pageable);

        GlobalSuccessResponseDto<Object> searchFileInfoSuccessResponse =
                globalService.successResponse(SEARCH_FILE_INFO_RETURN_SUCCESS.getMessage(),
                        searchFileInfoResponseDtoList);

        return ResponseEntity.status(OK).body(searchFileInfoSuccessResponse);
    }

    @GetMapping("/file/{workSpaceId}/topseven")
    public ResponseEntity<Object> topSevenFileList(@PathVariable(value = "workSpaceId") Long workSpaceId) {
        log.info("파일 생성 시간 순 최상단 7개 조회");
        List<TopSevenFileInfoResponseDto> topSevenFileInfoResponseDtoList =
                fileService.topSevenFileOrderByCreatedAt(workSpaceId);

        GlobalSuccessResponseDto<Object> topSevenFileInfoSuccessResponse =
                globalService.successResponse(TOP_SEVEN_FILE_INFO_RETURN_SUCCESS.getMessage(),
                        topSevenFileInfoResponseDtoList);
        return ResponseEntity.status(OK).body(topSevenFileInfoSuccessResponse);
    }
}
