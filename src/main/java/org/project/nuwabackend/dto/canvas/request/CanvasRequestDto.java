package org.project.nuwabackend.dto.canvas.request;

import lombok.Builder;

@Builder
public record CanvasRequestDto(String title, String content) {
}
