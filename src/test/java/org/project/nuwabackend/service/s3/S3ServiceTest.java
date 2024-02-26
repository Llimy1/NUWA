package org.project.nuwabackend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.dto.file.response.FileUploadResultDto;
import org.project.nuwabackend.type.FilePathType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.project.nuwabackend.type.FilePathType.FILE_PATH;
import static org.project.nuwabackend.type.FilePathType.IMAGE_PATH;

@DisplayName("[Service] S3 Service Test")
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    AmazonS3 amazonS3;
    @Mock
    MultipartFile multipartFile;

    @InjectMocks
    S3Service s3Service;

    String fileName = "test.jpg";
    String originFileName = "test";
    String fileExtension = ".jpg";
    Set<String> validExtensions = new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".svg"));

    @Test
    @DisplayName("[Service] S3 Upload Test")
    void s3UploadTest() throws IOException {
        //given
        Map<String, Long> imageUrlMap = new HashMap<>();

        String channelType = "direct";
        String uploadFileName = originFileName + "_" + LocalDateTime.now() + fileExtension;
        Long byteSize = 1024L;
        String contentType = "image/jpeg";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/image/direct/" + uploadFileName;
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        imageUrlMap.put(expectedUrl, byteSize);
        given(multipartFile.getOriginalFilename())
                .willReturn(fileName);
        given(multipartFile.getContentType())
                .willReturn(contentType);
        given(multipartFile.getSize())
                .willReturn(byteSize);
        given(multipartFile.getInputStream())
                .willReturn(inputStream);
        given(amazonS3.getUrl(any(), anyString()))
                .willReturn(new URL(expectedUrl));

        //when
        FileUploadResultDto fileUploadResultDto = s3Service.upload(channelType, List.of(multipartFile));

        //then
        fileUploadResultDto.uploadImageUrlMap().forEach((key, value) -> {
           assertThat(imageUrlMap.containsKey(key)).isTrue();
           assertThat(value).isEqualTo(imageUrlMap.get(key));
        });
    }

    @Test
    @DisplayName("[Service] Origin Name Test")
    void originFileNameTest() {
        //given
        given(multipartFile.getOriginalFilename())
                .willReturn(originFileName);

        //when
        String mockOriginalFilename = multipartFile.getOriginalFilename();

        //then
        assertThat(mockOriginalFilename).isEqualTo(originFileName);
    }

    @Test
    @DisplayName("[Service] Get Extension Test")
    void getExtensionTest() {
        //given
        int lastIndexOfDot = fileName.lastIndexOf(".");

        //when
        String subFileExtension = fileName.substring(lastIndexOfDot);

        //then
        assertThat(subFileExtension).isEqualTo(fileExtension);

    }

    @Test
    @DisplayName("[Service] Create File Name Test")
    void createFileNameTest() {
        //given
        int lastIndexOfDot = fileName.lastIndexOf(".");
        LocalDateTime now = LocalDateTime.now();

        String testName = "test_" + now + ".jpg";

        //when
        String createFileName = fileName.substring(0, lastIndexOfDot)
                .concat("_")
                .concat(now.toString())
                .concat(fileName.substring(lastIndexOfDot));

        //then
        assertThat(createFileName).isEqualTo(testName);
    }

    @Test
    @DisplayName("[Service] Get File Path Test")
    void getFilePathTest() {
        //given
        int lastIndexOfDot = fileName.lastIndexOf(".");
        String fileExtension = fileName.substring(lastIndexOfDot).toLowerCase();

        boolean contains = validExtensions.contains(fileExtension);

        //when
        FilePathType filePathType = contains ? IMAGE_PATH : FILE_PATH;

        //then
        assertThat(filePathType).isEqualTo(IMAGE_PATH);
    }

    @Test
    @DisplayName("[Service] Is Valid Image Extension Test")
    void isValidImageExtensionTest() {
        //given

        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        //when
        boolean contains = validExtensions.contains(fileExtension);

        //then
        assertThat(contains).isTrue();
    }
}