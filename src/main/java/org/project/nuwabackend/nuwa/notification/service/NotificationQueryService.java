package org.project.nuwabackend.nuwa.notification.service;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.notification.dto.response.NotificationListResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.project.nuwabackend.nuwa.domain.notification.QNotification.notification;
import static org.project.nuwabackend.nuwa.domain.workspace.QWorkSpaceMember.workSpaceMember;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final JPAQueryFactory jpaQueryFactory;

    public Slice<NotificationListResponseDto> notificationListResponseDtoSlice(String email, Long workSpaceId, Boolean isRead, Pageable pageable) {
        List<NotificationListResponseDto> notificationListResponseDtoList =
                jpaQueryFactory.select(notificationListResponseDto())
                        .from(notification)
                        .join(notification.receiver, workSpaceMember)
                        .where(
                                memberEmailEq(email),
                                workSpaceIdEq(workSpaceId),
                                isReadEq(isRead)
                        )
                        .orderBy(notification.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize() + 1)
                        .fetch();

        boolean hasNext = notificationListResponseDtoList.size() > pageable.getPageSize();
        List<NotificationListResponseDto> notificationContent = hasNext ? notificationListResponseDtoList.subList(0, pageable.getPageSize()) : notificationListResponseDtoList;

        return new SliceImpl<>(notificationContent, pageable, hasNext);
    }

    public List<NotificationListResponseDto> notificationV2(String email, Long workSpaceId, Boolean isRead) {
        return jpaQueryFactory.select(notificationListResponseDto())
                        .from(notification)
                        .join(notification.receiver, workSpaceMember)
                        .where(
                                memberEmailEq(email),
                                workSpaceIdEq(workSpaceId),
                                isReadEq(isRead)
                        )
                        .fetch();
    }

    private ConstructorExpression<NotificationListResponseDto> notificationListResponseDto() {
        return Projections.constructor(NotificationListResponseDto.class,
                notification.id.as("notificationId"),
                notification.content.as("notificationContent"),
                notification.url.as("notificationUrl"),
                notification.isRead.as("isRead"),
                notification.type.as("notificationType"),
                notification.sender.id.as("notificationSenderId"),
                notification.sender.name.as("notificationSenderName"),
                notification.receiver.id.as("notificationReceiverId"),
                notification.receiver.name.as("notificationReceiverName"),
                notification.createdAt.as("createdAt"));
    }

    private BooleanExpression memberEmailEq(String email) {
        return hasText(email) ? workSpaceMember.member.email.eq(email) : null;
    }

    private BooleanExpression workSpaceIdEq(Long workSpaceId) {
        return workSpaceMember.workSpace.id.eq(workSpaceId);
    }

    private BooleanExpression isReadEq(Boolean isRead) {
        return notification.isRead.eq(isRead);
    }
}
