package org.project.nuwabackend.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.dto.file.request.FileRequestDto;
import org.project.nuwabackend.dto.file.response.FileUploadIdResponseDto;
import org.project.nuwabackend.dto.file.response.FileUrlListResponse;
import org.project.nuwabackend.global.annotation.MemberEmail;
import org.project.nuwabackend.global.dto.GlobalSuccessResponseDto;
import org.project.nuwabackend.global.service.GlobalService;
import org.project.nuwabackend.service.s3.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.project.nuwabackend.global.type.SuccessMessage.FILE_UPLOAD_SUCCESS;
import static org.project.nuwabackend.global.type.SuccessMessage.FILE_URL_RETURN_SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
// TODO: test code
public class FileController {

    private final FileService fileService;
    private final GlobalService globalService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@MemberEmail String email,
                                              @RequestPart(name = "fileList") List<MultipartFile> multipartFileList,
                                              @RequestPart(name = "fileRequestDto") FileRequestDto fileRequestDto) {
        log.info("파일 업로드 API 호출");
        FileUploadIdResponseDto uploadIdResponse = fileService.upload(email, multipartFileList, fileRequestDto);

        GlobalSuccessResponseDto<Object> uploadImageSuccessResponse =
                globalService.successResponse(FILE_UPLOAD_SUCCESS.getMessage(), uploadIdResponse);

        return ResponseEntity.status(CREATED).body(uploadImageSuccessResponse);
    }

    @GetMapping("/upload")
    public ResponseEntity<Object> uploadFileUrlList(@RequestParam(required = false) List<Long> fileIdList,
                                                    @RequestParam(required = false) List<Long> imageIdList) {
        log.info("직전에 업로드한 파일 URL 조회 API 호출");
        FileUrlListResponse fileUrlListResponse = fileService.fileUrlList(fileIdList, imageIdList);
        GlobalSuccessResponseDto<Object> uploadFileUrlListSuccessResponse =
                globalService.successResponse(FILE_URL_RETURN_SUCCESS.getMessage(), fileUrlListResponse);

        return ResponseEntity.status(OK).body(uploadFileUrlListSuccessResponse);
    }
}
