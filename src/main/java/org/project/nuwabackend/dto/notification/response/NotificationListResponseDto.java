package org.project.nuwabackend.dto.notification.response;

import lombok.Builder;
import org.project.nuwabackend.type.NotificationType;

import java.time.LocalDateTime;

@Builder
public record NotificationListResponseDto(Long notificationId,
                                          String notificationContent,
                                          String notificationUrl,
                                          Boolean isRead,
                                          NotificationType notificationType,
                                          LocalDateTime createdAt) {
}
