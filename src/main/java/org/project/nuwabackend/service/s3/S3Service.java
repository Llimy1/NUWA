package org.project.nuwabackend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.dto.file.response.FileUploadResultDto;
import org.project.nuwabackend.type.S3PathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.project.nuwabackend.global.type.ErrorMessage.FILE_EXTENSION_NOT_FOUND;
import static org.project.nuwabackend.type.S3PathType.FILE_PATH;
import static org.project.nuwabackend.type.S3PathType.IMAGE_PATH;

@RequiredArgsConstructor
@Service
// TODO: test code
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // TODO: test code & 추후 리팩토링
    // 파일 업로드 (이미지 & 파일)
    public FileUploadResultDto upload(String channelType, List<MultipartFile> multipartFileList) {

        List<String> imageUrlList = new ArrayList<>();
        List<String> fileUrlList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            String originFileName = originFileName(multipartFile);

            String uploadFileName = createFileName(originFileName);
            S3PathType filePath = getFilePath(uploadFileName);

            String fileContentType = multipartFile.getContentType();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(fileContentType);

            try (InputStream inputStream = multipartFile.getInputStream()) {
                switch (filePath) {
                    case IMAGE_PATH -> {
                        switch (channelType.toLowerCase()) {
                            case "direct" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/image/direct", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                imageUrlList.add(amazonS3.getUrl(bucket + "/image/direct", uploadFileName).toString());
                            }
                            case "chat" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/image/chat", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                imageUrlList.add(amazonS3.getUrl(bucket + "/image/chat", uploadFileName).toString());
                            }
                            case "voice" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/image/voice", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                imageUrlList.add(amazonS3.getUrl(bucket + "/image/voice", uploadFileName).toString());
                            }
                        }
                    } case FILE_PATH -> {
                        switch (channelType.toLowerCase()) {
                            case "direct" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/file/direct", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                fileUrlList.add(amazonS3.getUrl(bucket + "/file/direct", uploadFileName).toString());
                            }
                            case "chat" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/file/chat", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                fileUrlList.add(amazonS3.getUrl(bucket + "/file/chat", uploadFileName).toString());
                            }
                            case "voice" -> {
                                amazonS3.putObject(new PutObjectRequest(bucket + "/file/voice", uploadFileName, inputStream, objectMetadata)
                                        .withCannedAcl(CannedAccessControlList.PublicRead));
                                fileUrlList.add(amazonS3.getUrl(bucket + "/file/voice", uploadFileName).toString());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // TODO: 예외 처리
                throw new IllegalStateException(e.getMessage());
            }
        }
        return new FileUploadResultDto(imageUrlList, fileUrlList);
    }

    // 기존 파일 이름
    private String originFileName(MultipartFile multipartFile) {
        return multipartFile.getOriginalFilename();
    }

    // 이미지 파일명 중복 방지
    private String createFileName(String fileName) {
        return fileName.substring(0, fileName.indexOf("."))
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
    private S3PathType getFilePath(String fileName) {
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
