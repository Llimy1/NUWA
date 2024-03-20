package org.project.nuwabackend.nuwa.workspace.dto.response.inquiry;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.project.nuwabackend.nuwa.canvas.dto.response.CanvasResponseDto;
import org.project.nuwabackend.nuwa.file.dto.response.FileSearchInfoResponseDto;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // DTO 를 JSON으로 변환 시 null값인 field 제외
public record WorkSpaceContentSearchResponseDto(
        List<FileSearchInfoResponseDto> fileSearchInfoResponseDtoList,
        List<CanvasResponseDto> canvasResponseDtoList) {
}
