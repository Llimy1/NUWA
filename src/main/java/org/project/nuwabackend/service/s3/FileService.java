package org.project.nuwabackend.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.channel.Channel;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.multimedia.File;
import org.project.nuwabackend.domain.multimedia.Image;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.file.request.FileRequestDto;
import org.project.nuwabackend.dto.file.response.FileUploadIdResponseDto;
import org.project.nuwabackend.dto.file.response.FileUploadResultDto;
import org.project.nuwabackend.dto.file.response.FileUrlListResponse;
import org.project.nuwabackend.dto.file.response.FileUrlResponseDto;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.jpa.ChannelRepository;
import org.project.nuwabackend.repository.jpa.FileRepository;
import org.project.nuwabackend.repository.jpa.ImageRepository;
import org.project.nuwabackend.repository.jpa.MemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.MEMBER_ID_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.global.type.ErrorMessage.WORK_SPACE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// TODO: test code
public class FileService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final ChannelRepository channelRepository;
    private final ImageRepository imageRepository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;

    @Transactional
    public FileUploadIdResponseDto upload(String email, List<MultipartFile> multipartFileList, FileRequestDto fileRequestDto) {
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

        List<String> fileUrlList = fileUploadResultDto.uploadFileUrlList();
        List<String> imageUrlList = fileUploadResultDto.uploadImageUrlList();

        List<Long> imageIdList = new ArrayList<>();
        List<Long> fileIdList = new ArrayList<>();

        // 이미지 URL 리스트가 비어 있지 않은 경우 처리
        if (!imageUrlList.isEmpty()) {
            List<Image> imageList = new ArrayList<>();
            for (String imageUrl : imageUrlList) {
                Image image = Image.createImage(imageUrl, findWorkSpaceMember, findWorkSpace, findChannel);
                imageList.add(image);
            }
            List<Image> savedImageList = imageRepository.saveAll(imageList);
            savedImageList.forEach(image -> imageIdList.add(image.getId()));
        }

        // 파일 URL 리스트가 비어 있지 않은 경우 처리
        if (!fileUrlList.isEmpty()) {
            List<File> fileList = new ArrayList<>();
            for (String fileUrl : fileUrlList) {
                File file = File.createFile(fileUrl, findWorkSpaceMember, findWorkSpace, findChannel);
                fileList.add(file);
            }
            List<File> savedFileList = fileRepository.saveAll(fileList);
            savedFileList.forEach(file -> fileIdList.add(file.getId()));
        }

        return new FileUploadIdResponseDto(fileIdList, imageIdList);
    }

    // 이미지와 파일 url 조회
    public FileUrlListResponse fileUrlList(List<Long> fileIdList, List<Long> imageIdList) {

        List<File> fileList = fileRepository.findByIdIn(fileIdList);
        List<Image> imageList = imageRepository.findByIdIn(imageIdList);

        List<FileUrlResponseDto> fileUrlListResponseList = new ArrayList<>();
        List<FileUrlResponseDto> imageUrlListResponseList = new ArrayList<>();

        fileList.forEach(file -> {
            fileUrlListResponseList.add(new FileUrlResponseDto(file.getId(), file.getUrl()));
        });

        imageList.forEach(image -> {
            imageUrlListResponseList.add(new FileUrlResponseDto(image.getId(), image.getUrl()));
        });


        return new FileUrlListResponse(fileUrlListResponseList, imageUrlListResponseList);
    }
}
