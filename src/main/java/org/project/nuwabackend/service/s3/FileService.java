package org.project.nuwabackend.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Channel;
import org.project.nuwabackend.domain.multimedia.File;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.file.request.FileRequestDto;
import org.project.nuwabackend.dto.file.response.FileInfoResponseDto;
import org.project.nuwabackend.dto.file.response.FileUploadResponseDto;
import org.project.nuwabackend.dto.file.response.FileUploadResultDto;
import org.project.nuwabackend.dto.file.response.FileUrlResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChannelRepository;
import org.project.nuwabackend.repository.jpa.FileRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.project.nuwabackend.type.FileType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// TODO: test code
// TODO: API 명세서 수정
public class FileService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final ChannelRepository channelRepository;
    private final FileRepository fileRepository;

    private final FileQueryService fileQueryService;
    private final S3Service s3Service;

    @Transactional
    public List<FileUploadResponseDto> upload(String email, List<MultipartFile> multipartFileList, FileRequestDto fileRequestDto) {
        log.info("업로드 (이미지 or 파일)");
        Long workSpaceId = fileRequestDto.workSpaceId();
        Long channelId = fileRequestDto.channelId();

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpace findWorkSpace = findWorkSpaceMember.getWorkSpace();

        Channel findChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        String dtype = findChannel.getClass().getSimpleName();

        FileUploadResultDto fileUploadResultDto = s3Service.upload(dtype, multipartFileList);

        Map<String, Long> fileUrlMap = fileUploadResultDto.uploadFileUrlList();
        Map<String, Long> imageUrlMap = fileUploadResultDto.uploadImageUrlList();

        List<File> fileList = new ArrayList<>();

        // 이미지 URL 리스트가 비어 있지 않은 경우 처리
        if (!imageUrlMap.isEmpty()) {

            imageUrlMap.forEach((key, value) -> {

                String originFileName = getOriginFileName(key);
                String extension = getExtension(key);

                File file = File.createFile(key, originFileName, value, extension, FileType.IMAGE,
                                findWorkSpaceMember, findWorkSpace, findChannel);
                fileList.add(file);
            });
        }

        // 파일 URL 리스트가 비어 있지 않은 경우 처리
        if (!fileUrlMap.isEmpty()) {
            fileUrlMap.forEach((key, value) -> {

                String originFileName = getOriginFileName(key);
                String extension = getExtension(key);

                File file = File.createFile(key, originFileName, value, extension, FileType.FILE,
                        findWorkSpaceMember, findWorkSpace, findChannel);

                fileList.add(file);
            });
        }

        List<File> savefileList = fileRepository.saveAll(fileList);

        return savefileList.stream().map(file -> FileUploadResponseDto.builder()
                .fileId(file.getId())
                .fileType(file.getFileType())
                .build())
                .toList();
    }

    // 이미지와 파일 url 조회
    public List<FileUrlResponseDto> fileUrlList(List<Long> fileIdList) {

        List<File> fileList = fileRepository.findByIdIn(fileIdList);

        return fileList.stream().map(file -> FileUrlResponseDto.builder()
                .fileId(file.getId())
                .fileUrl(file.getUrl())
                .fileType(file.getFileType())
                .fileCreatedAt(file.getCreatedAt())
                .build())
                .toList();
    }

    // 파일 조회
    public Slice<FileInfoResponseDto> fileList(Long workSpaceId, String fileExtension, FileType fileType, Pageable pageable) {
        log.info("파일 조회");
        return fileQueryService.fileList(workSpaceId, fileExtension, fileType, pageable);
    }

    // 파일 검색
    public Slice<FileInfoResponseDto> searchFileName(Long workSpaceId, String fileName, String fileExtension, FileType fileType, Pageable pageable) {
        log.info("파일 검색");
        return fileQueryService.searchFileName(workSpaceId, fileName, fileExtension, fileType, pageable);
    }

    // 파일 원본 이름
    private String getOriginFileName(String fileUrl) {
        int slashIndex = fileUrl.lastIndexOf("/") + 1;
        int underBarIndex = fileUrl.lastIndexOf("_");

        String decode = URLDecoder.decode(fileUrl.substring(slashIndex, underBarIndex), StandardCharsets.UTF_8).trim();

        return Normalizer.normalize(decode, Normalizer.Form.NFC);
    }

    // 파일 확장자
    private String getExtension(String fileUrl) {
        int dotIndex = fileUrl.lastIndexOf(".") + 1;

        return fileUrl.substring(dotIndex);
    }
}
