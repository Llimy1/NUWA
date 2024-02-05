package org.project.nuwabackend.domain.mongo;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.base.BaseTimeMongo;
import org.project.nuwabackend.type.MediaType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("direct")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectMessage extends BaseTimeMongo {

    @Id
    private String id;

    @Field(name = "direct_room_id")
    private String roomId;

    @Field(name = "direct_sender")
    private String sender;

    @Field(name = "direct_receiver")
    private String receiver;

    @Field(name = "direct_content")
    private String content;

    @Field(name = "direct_media_type")
    private String type;

    @Field(name = "direct_is_read")
    private Boolean isRead;

    @Builder
    private DirectMessage(String roomId, String sender, String receiver, String content, MediaType type) {
        this.roomId = roomId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type.name();
    }

    public static DirectMessage createDirectMessage(String roomId, String sender, String receiver, String content, MediaType type) {
        return DirectMessage.builder()
                .roomId(roomId)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .type(type)
                .build();
    }
}
