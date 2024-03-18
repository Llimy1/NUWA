package org.project.nuwabackend.dto.workspace.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.project.nuwabackend.dto.canvas.response.CanvasResponseDto;
import org.project.nuwabackend.dto.file.response.FileSearchInfoResponseDto;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // DTO 를 JSON으로 변환 시 null값인 field 제외
public record WorkSpaceContentSearchResponseDto(
        List<FileSearchInfoResponseDto> fileSearchInfoResponseDtoList,
        List<CanvasResponseDto> canvasResponseDtoList) {
}
