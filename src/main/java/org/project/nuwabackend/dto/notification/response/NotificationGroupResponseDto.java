package org.project.nuwabackend.dto.notification.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.type.NotificationType;

import java.time.LocalDateTime;
import java.util.List;


@Builder
public record NotificationGroupResponseDto(List<Long> notificationIdList,
                                           Long contentCount,
                                           Long senderId,
                                           String senderName,
                                           String notificationUrl,
                                           NotificationType notificationType,
                                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                           LocalDateTime createdAt) {
}
