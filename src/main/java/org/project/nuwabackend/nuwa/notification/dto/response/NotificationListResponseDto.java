package org.project.nuwabackend.nuwa.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.nuwa.notification.type.NotificationType;

import java.time.LocalDateTime;

@Builder
public record NotificationListResponseDto(Long notificationId,
                                          String notificationContent,
                                          String notificationUrl,
                                          Boolean isRead,
                                          NotificationType notificationType,
                                          Long notificationSenderId,
                                          String notificationSenderName,
                                          Long notificationReceiverId,
                                          String notificationReceiverName,
                                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                          LocalDateTime createdAt) {
}
