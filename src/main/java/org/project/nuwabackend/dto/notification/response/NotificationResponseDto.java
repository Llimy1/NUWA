package org.project.nuwabackend.dto.notification.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponseDto(Long workSpaceId,
                                      Long notificationId,
                                      String notificationContent,
                                      String notificationUrl,
                                      Boolean isRead,
                                      String notificationType,
                                      Long notificationWorkSpaceId,
                                      String notificationWorkSpaceName,
                                      LocalDateTime createdAt) {
}
