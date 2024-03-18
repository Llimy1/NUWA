package org.project.nuwabackend.dto.file.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.type.FileType;
import org.project.nuwabackend.type.FileUploadType;

import java.time.LocalDateTime;

@Builder
public record FileSearchInfoResponseDto(Long fileId,
                                        String fileUrl,
                                        String fileName,
                                        FileUploadType fileUploadType,
                                        FileType fileType,
                                        Long fileMemberUploadId,
                                        String fileMemberUploadName,
                                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                        LocalDateTime createdAt) {
}
