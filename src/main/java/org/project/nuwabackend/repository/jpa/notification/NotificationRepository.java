package org.project.nuwabackend.repository.jpa.notification;

import org.project.nuwabackend.domain.notification.Notification;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
