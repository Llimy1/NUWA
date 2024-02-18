package org.project.nuwabackend.dto.file.response;

import lombok.Builder;

import java.util.List;

@Builder
public record FileUploadIdResponseDto(List<Long> fileIdList, List<Long> imageIdList) {
}
