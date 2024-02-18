package org.project.nuwabackend.dto.file.response;

import java.util.List;

public record FileUrlListResponse(List<FileUrlResponseDto> fileUrlResponseDtoList, List<FileUrlResponseDto> imageUrlResponseDtoList) {
}
