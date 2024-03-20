package org.project.nuwabackend.nuwa.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class DirectMessageResponseDto {
        @Setter
        private String messageId;
        private Long workSpaceId;
        private String roomId;
        private Long senderId;
        private String senderName;
        private String content;
        private List<String> rawString;
        private Long readCount;
        @Setter
        private Boolean isEdited;
        @Setter
        private Boolean isDeleted;
        private MessageType messageType;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        private LocalDateTime createdAt;

        @Builder
        public DirectMessageResponseDto(String messageId, Long workSpaceId, String roomId, Long senderId, String senderName, String content, List<String> rawString, Long readCount, Boolean isEdited, Boolean isDeleted, MessageType messageType, LocalDateTime createdAt) {
                this.messageId = messageId;
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
}
