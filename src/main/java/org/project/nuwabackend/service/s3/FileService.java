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
import org.project.nuwabackend.dto.file.response.TopSevenFileInfoResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChannelRepository;
import org.project.nuwabackend.repository.jpa.FileRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.type.FileType;
import org.project.nuwabackend.type.FileUploadType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.FILE_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final ChannelRepository channelRepository;
    private final FileRepository fileRepository;

    private final FileQueryService fileQueryService;
    private final S3Service s3Service;

    @Transactional
    public List<FileUploadResponseDto> upload(String email, FileType fileType, Long channelId, List<MultipartFile> multipartFileList, FileRequestDto fileRequestDto) {
        log.info("업로드 (이미지 or 파일)");
        Long workSpaceId = fileRequestDto.workSpaceId();

        WorkSpaceMember findWorkSpaceMember = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        WorkSpace findWorkSpace = findWorkSpaceMember.getWorkSpace();

        // 일반 API와 채널에 대한 업로드가 필요
        FileUploadResultDto fileUploadResultDto = s3Service.upload(fileType, multipartFileList);

        Map<String, Long> fileUrlMap = fileUploadResultDto.uploadFileUrlMap();
        Map<String, Long> imageUrlMap = fileUploadResultDto.uploadImageUrlMap();


        if (!fileType.equals(FileType.CANVAS)) {
            Channel findChannel = channelRepository.findById(channelId)
                    .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

            return channelUploadList(fileUrlMap, imageUrlMap, fileType, findWorkSpaceMember, findWorkSpace, findChannel);
        }

        return basicUploadList(fileUrlMap, imageUrlMap, fileType, findWorkSpaceMember, findWorkSpace);
    }

    // 이미지와 파일 url 조회
    public List<FileUrlResponseDto> fileUrlList(List<Long> fileIdList) {

        List<File> fileList = fileRepository.findByIdIn(fileIdList);

        return fileList.stream().map(file -> FileUrlResponseDto.builder()
                        .fileId(file.getId())
                        .fileUrl(file.getUrl())
                        .fileUploadType(file.getFileUploadType())
                        .fileType(file.getFileType())
                        .fileCreatedAt(file.getCreatedAt())
                        .build())
                .toList();
    }

    // 파일 조회
    public Slice<FileInfoResponseDto> fileList(Long workSpaceId, String fileExtension, FileUploadType fileUploadType, Pageable pageable) {
        log.info("파일 조회");
        return fileQueryService.fileList(workSpaceId, fileExtension, fileUploadType, pageable);
    }

    // 파일 검색
    public Slice<FileInfoResponseDto> searchFileName(Long workSpaceId, String fileName, String fileExtension, FileUploadType fileUploadType, Pageable pageable) {
        log.info("파일 검색");
        return fileQueryService.searchFileName(workSpaceId, fileName, fileExtension, fileUploadType, pageable);
    }

    // 최근 파일 조회 (7개)
    public List<TopSevenFileInfoResponseDto> topSevenFileOrderByCreatedAt(Long workSpaceId) {
        log.info("최근 생성 시간 순 7개 파일 조회");
        return fileQueryService.topSevenFileOrderByCreatedAt(workSpaceId);
    }

    // 워크스페이스 id로 해당된 모든 파일 삭제
    // TODO: integrated test code
    @Transactional
    public void deleteFileWorkSpaceId(Long workSpaceId) {
        log.info("모든 파일 삭제");
        List<File> findFileList = fileRepository.findByWorkSpaceId(workSpaceId);

        findFileList.forEach(file -> {
            s3Service.deleteFile(file.getUrl(), file.getFileType());
        });

        fileRepository.deleteByWorkSpaceId(workSpaceId);
    }

    // 파일 ID로 파일 삭제
    // TODO: integrated test code
    @Transactional
    public Map<String, String> deleteFile(Long fileId) {
        log.info("파일 삭제");
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException(FILE_NOT_FOUND));
        s3Service.deleteFile(file.getUrl(), file.getFileType());
        fileRepository.delete(file);

        Map<String, String> deleteMap = new HashMap<>();
        deleteMap.put(file.getFileType().getValue(), file.getUrl());

        return deleteMap;
    }

    public List<Long> fileIdList(List<String> fileUrlList) {
        log.info("파일 ID 가져오기");
        List<Long> fileIdList = new ArrayList<>();
        List<File> fileByUrlIn =
                fileRepository.findFileByUrlIn(fileUrlList);

        fileByUrlIn.forEach(file -> {
            fileIdList.add(file.getId());
        });

        return fileIdList;
    }

    // TODO: integrated test code
    // WorkSPaceId와 RoomId에 해당되는 파일 전부 삭제
    public void deleteFileByWorkSpaceIdAndRoomId(Long workSpaceId, String roomId) {
        fileRepository.deleteByWorkSpaceIdAndChannelRoomId(workSpaceId, roomId);
    }

    // 파일 원본 이름
    private static String getOriginFileName(String fileUrl) {
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

    // 일반 파일 리스트 반환
    private List<FileUploadResponseDto> basicUploadList(Map<String, Long> fileUrlMap, Map<String, Long> imageUrlMap,
                                  FileType fileType, WorkSpaceMember workSpaceMember, WorkSpace workSpace) {
        List<File> fileList = new ArrayList<>();

        // 이미지 URL 리스트가 비어 있지 않은 경우 처리
        if (!imageUrlMap.isEmpty()) {

            imageUrlMap.forEach((key, value) -> {

                String originFileName = getOriginFileName(key);
                String extension = getExtension(key);

                File file = File.createFile(key, originFileName, value, extension, FileUploadType.IMAGE, fileType,
                        workSpaceMember, workSpace);
                fileList.add(file);
            });
        }

        // 파일 URL 리스트가 비어 있지 않은 경우 처리
        if (!fileUrlMap.isEmpty()) {
            fileUrlMap.forEach((key, value) -> {

                String originFileName = getOriginFileName(key);
                String extension = getExtension(key);

                File file = File.createFile(key, originFileName, value, extension, FileUploadType.FILE, fileType,
                        workSpaceMember, workSpace);

                fileList.add(file);
            });
        }
        return fileRepository.saveAll(fileList).stream().map(file -> FileUploadResponseDto.builder()
                        .fileId(file.getId())
                        .fileUploadType(file.getFileUploadType())
                        .fileType(file.getFileType())
                        .build())
                .toList();
    }


    // 채널 파일 리스트 반환
    private List<FileUploadResponseDto> channelUploadList(Map<String, Long> fileUrlMap, Map<String, Long> imageUrlMap,
                                                       FileType fileType, WorkSpaceMember workSpaceMember, WorkSpace workSpace, Channel channel) {

        List<File> fileList = new ArrayList<>();

        // 이미지 URL 리스트가 비어 있지 않은 경우 처리
        if (!imageUrlMap.isEmpty()) {

            imageUrlMap.forEach((key, value) -> {

                String originFileName = getOriginFileName(key);
                String extension = getExtension(key);

                File file = File.createChannelFile(key, originFileName, value, extension, FileUploadType.IMAGE, fileType,
                        workSpaceMember, workSpace, channel);
                fileList.add(file);
            });
        }

        // 파일 URL 리스트가 비어 있지 않은 경우 처리
        if (!fileUrlMap.isEmpty()) {
            fileUrlMap.forEach((key, value) -> {

                String originFileName = getOriginFileName(key);
                String extension = getExtension(key);

                File file = File.createChannelFile(key, originFileName, value, extension, FileUploadType.FILE, fileType,
                        workSpaceMember, workSpace, channel);

                fileList.add(file);
            });
        }
        return fileRepository.saveAll(fileList).stream().map(file -> FileUploadResponseDto.builder()
                        .fileId(file.getId())
                        .fileUrl(file.getUrl())
                        .fileUploadType(file.getFileUploadType())
                        .fileType(file.getFileType())
                        .build())
                .toList();
    }
}
