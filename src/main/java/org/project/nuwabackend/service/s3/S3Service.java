package org.project.nuwabackend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.dto.file.response.FileUploadResultDto;
import org.project.nuwabackend.type.FilePathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.project.nuwabackend.global.type.ErrorMessage.FILE_EXTENSION_NOT_FOUND;
import static org.project.nuwabackend.type.FilePathType.FILE_PATH;
import static org.project.nuwabackend.type.FilePathType.IMAGE_PATH;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 (이미지 & 파일)
    public FileUploadResultDto upload(String channelType, List<MultipartFile> multipartFileList) {

        Map<String, Long> imageUrlMap = new HashMap<>();
        Map<String, Long> fileUrlMap = new HashMap<>();

        for (MultipartFile multipartFile : multipartFileList) {
            String originFileName = originFileName(multipartFile);

            String uploadFileName = createFileName(originFileName);
            FilePathType filePath = getFilePath(uploadFileName);
            Long byteSize = multipartFile.getSize();

            String fileContentType = multipartFile.getContentType();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(byteSize);
            objectMetadata.setContentType(fileContentType);

            try (InputStream inputStream = multipartFile.getInputStream()) {
                switch (filePath) {
                    case IMAGE_PATH -> {
                        switch (channelType.toLowerCase()) {
                            case "direct" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/image/direct", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                imageUrlMap.put(amazonS3.getUrl(bucket + "/image/direct", uploadFileName).toString(), byteSize);
                            }
                            case "chat" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/image/chat", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                imageUrlMap.put(amazonS3.getUrl(bucket + "/image/chat", uploadFileName).toString(), byteSize);
                            }
                            case "voice" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/image/voice", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                imageUrlMap.put(amazonS3.getUrl(bucket + "/image/voice", uploadFileName).toString(), byteSize);
                            }
                        }
                    } case FILE_PATH -> {
                        switch (channelType.toLowerCase()) {
                            case "direct" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/file/direct", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                fileUrlMap.put(amazonS3.getUrl(bucket + "/file/direct", uploadFileName).toString(), byteSize);
                            }
                            case "chat" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/file/chat", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                fileUrlMap.put(amazonS3.getUrl(bucket + "/file/chat", uploadFileName).toString(), byteSize);
                            }
                            case "voice" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/file/voice", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                fileUrlMap.put(amazonS3.getUrl(bucket + "/file/voice", uploadFileName).toString(), byteSize);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // TODO: 예외 처리
                throw new IllegalStateException(e.getMessage());
            }
        }
        return new FileUploadResultDto(imageUrlMap, fileUrlMap);
    }

    // 기존 파일 이름
    private String originFileName(MultipartFile multipartFile) {
        return multipartFile.getOriginalFilename();
    }

    // 이미지 파일명 중복 방지
    private String createFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."))
                .concat("_")
                .concat(LocalDateTime.now().toString())
                .concat(getExtension(fileName));
    }

    // 확장자 가져오기
    private String getExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            throw new IllegalArgumentException(FILE_EXTENSION_NOT_FOUND.getMessage());
        }

        return fileName.substring(lastIndexOfDot);
    }

    // 확장자로 파일인지 이미지인지 확인
    private FilePathType getFilePath(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            throw new IllegalArgumentException(FILE_EXTENSION_NOT_FOUND.getMessage());
        }

        String fileExtension = fileName.substring(lastIndexOfDot).toLowerCase();
        if (!isValidImageExtension(fileExtension)) {
            return FILE_PATH;
        }

        return IMAGE_PATH;
    }

    // 확장자 확인
    private boolean isValidImageExtension(String fileName) {
        Set<String> validExtensions = new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".svg"));
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        return validExtensions.contains(fileExtension);
    }
}
