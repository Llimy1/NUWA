package org.project.nuwabackend.nuwa.canvas.dto.request;

import lombok.Builder;

@Builder
public record CanvasRequestDto(String title, String content) {
}
