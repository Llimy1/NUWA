package org.project.nuwabackend.dto.file.response;

import lombok.Builder;
import org.project.nuwabackend.type.FileType;

@Builder
public record FileUploadResponseDto(Long fileId, FileType fileType) {
}
