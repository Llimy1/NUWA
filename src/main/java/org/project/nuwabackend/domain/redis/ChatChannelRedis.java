package org.project.nuwabackend.domain.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Objects;

@Getter
@NoArgsConstructor
@RedisHash(value = "chatChannel")
// TODO: test code
public class ChatChannelRedis {

    @Id
    private String id;

    @Indexed
    private String chatRoomId;
    @Indexed
    private String email;

    @Builder
    private ChatChannelRedis(String chatRoomId, String email) {
        this.chatRoomId = chatRoomId;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatChannelRedis that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static ChatChannelRedis createChatChannelRedis(String chatRoomId, String email) {
        return ChatChannelRedis.builder()
                .chatRoomId(chatRoomId)
                .email(email)
                .build();
    }


}
