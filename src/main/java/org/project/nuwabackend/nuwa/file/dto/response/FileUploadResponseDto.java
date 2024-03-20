package org.project.nuwabackend.nuwa.file.dto.response;

import lombok.Builder;
import org.project.nuwabackend.nuwa.file.type.FileType;
import org.project.nuwabackend.nuwa.file.type.FileUploadType;

@Builder
public record FileUploadResponseDto(Long fileId, String fileUrl, FileUploadType fileUploadType, FileType fileType) {
}
