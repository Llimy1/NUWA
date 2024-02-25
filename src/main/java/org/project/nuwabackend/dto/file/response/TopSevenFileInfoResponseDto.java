package org.project.nuwabackend.dto.file.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.type.FileType;

import java.time.LocalDateTime;

@Builder
public record TopSevenFileInfoResponseDto(Long fileId,
                                          String fileName,
                                          Long fileSize,
                                          String fileExtension,
                                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                     LocalDateTime createdAt) {
}
