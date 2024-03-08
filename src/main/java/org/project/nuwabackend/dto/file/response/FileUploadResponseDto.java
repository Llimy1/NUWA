package org.project.nuwabackend.dto.file.response;

import lombok.Builder;
import org.project.nuwabackend.type.FileType;
import org.project.nuwabackend.type.FileUploadType;

@Builder
public record FileUploadResponseDto(Long fileId, String fileUrl, FileUploadType fileUploadType, FileType fileType) {
}
