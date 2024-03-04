package org.project.nuwabackend.dto.channel.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
public class DirectChannelResponseDto {

    private String roomId;
    private String name;
    private Long workSpaceId;
    private Long createMemberId;
    private Long joinMemberId;
    private String createMemberName;
    private String joinMemberName;
    private Long unReadCount;

    @Setter
    private String lastMessage;
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime messageCreatedAt;

    @Builder
    public DirectChannelResponseDto(String roomId, String name, Long workSpaceId, Long createMemberId, Long joinMemberId, String createMemberName, String joinMemberName, Long unReadCount) {
        this.roomId = roomId;
        this.name = name;
        this.workSpaceId = workSpaceId;
        this.createMemberId = createMemberId;
        this.joinMemberId = joinMemberId;
        this.createMemberName = createMemberName;
        this.joinMemberName = joinMemberName;
        this.unReadCount = unReadCount;
    }
}
