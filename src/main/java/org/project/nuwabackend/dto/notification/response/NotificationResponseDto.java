package org.project.nuwabackend.dto.notification.response;

import lombok.Builder;
import org.project.nuwabackend.type.NotificationType;

import java.time.LocalDateTime;

@Builder
public record NotificationResponseDto(Long workSpaceId,
                                      Long notificationId,
                                      String notificationContent,
                                      String notificationUrl,
                                      Boolean isRead,
                                      NotificationType notificationType,
                                      Long notificationSenderId,
                                      String notificationSenderName,
                                      Long notificationReceiverId,
                                      String notificationReceiverName,
                                      LocalDateTime createdAt) {
}
