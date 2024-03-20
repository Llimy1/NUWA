package org.project.nuwabackend.nuwa.channel.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatChannelListResponseDto(Long workSpaceId,
                                         Long channelId,
                                         String roomId,
                                         String name,
                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                         LocalDateTime createdAt) {
}
