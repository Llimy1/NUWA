package org.project.nuwabackend.dto.canvas.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CanvasResponseDto(
        String canvasId,
        String canvasTitle,
        String canvasContent,
        Long workSpaceId,
        Long createMemberId,
        String createMemberName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime createdAt) {
}
