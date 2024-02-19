package org.project.nuwabackend.domain.mongo;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.base.BaseTimeMongo;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Document("direct")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectMessage extends BaseTimeMongo {

    @Id
    private String id;

    @Field(name = "direct_room_id")
    private String roomId;

    @Field(name = "direct_sender_id")
    private Long senderId;

    @Field(name = "direct_sender_name")
    private String senderName;

    @Field(name = "direct_content")
    private String content;

    @Field(name = "direct_read_count")
    private Long readCount;

    @Builder
    private DirectMessage(String roomId, Long senderId, String senderName, String content, Long readCount) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.readCount = readCount;
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

    public static DirectMessage createDirectMessage(String roomId, Long senderId, String senderName, String content, Long readCount) {
        return DirectMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content)
                .readCount(readCount)
                .build();
    }
}
