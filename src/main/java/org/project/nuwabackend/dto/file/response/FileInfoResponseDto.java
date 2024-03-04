package org.project.nuwabackend.dto.file.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.type.FileType;
import org.project.nuwabackend.type.FileUploadType;

import java.time.LocalDateTime;

@Builder
public record FileInfoResponseDto(Long fileId,
                                  String fileUrl,
                                  String fileName,
                                  Long fileSize,
                                  String fileExtension,
                                  FileUploadType fileUploadType,
                                  FileType fileType,
                                  Long fileMemberUploadId,
                                  String fileMemberUploadName,
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                     LocalDateTime createdAt) {
}
