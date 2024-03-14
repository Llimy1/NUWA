package org.project.nuwabackend.domain.mongo;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.type.MessageType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Document("direct")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectMessage {

    @Id
    private String id;

    @Field(name = "workspace_id")
    private Long workSpaceId;

    @Field(name = "direct_room_id")
    private String roomId;

    @Field(name = "direct_sender_id")
    private Long senderId;

    @Field(name = "direct_sender_name")
    private String senderName;

    @Field(name = "direct_content")
    private String content;

    @Field(name = "raw_string")
    private List<String> rawString;

    @Field(name = "direct_read_count")
    private Long readCount;

    @Field(name = "is_edited")
    private Boolean isEdited;

    @Field(name = "is_deleted")
    private Boolean isDeleted;

    @Field(name = "message_type")
    private MessageType messageType;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public DirectMessage(Long workSpaceId, String roomId, Long senderId, String senderName, String content, List<String> rawString, Long readCount, Boolean isEdited, Boolean isDeleted, MessageType messageType, LocalDateTime createdAt) {
        this.workSpaceId = workSpaceId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.rawString = rawString;
        this.readCount = readCount;
        this.isEdited = isEdited;
        this.isDeleted = isDeleted;
        this.messageType = messageType;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectMessage that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static DirectMessage createDirectMessage(Long workSpaceId, String roomId, Long senderId, String senderName, String content, List<String> rawString, Long readCount, MessageType messageType, LocalDateTime createdAt) {
        return DirectMessage.builder()
                .workSpaceId(workSpaceId)
                .roomId(roomId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content)
                .rawString(rawString)
                .readCount(readCount)
                .isEdited(false)
                .isDeleted(false)
                .messageType(messageType)
                .createdAt(createdAt)
                .build();
    }
}
