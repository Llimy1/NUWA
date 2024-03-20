package org.project.nuwabackend.nuwa.domain.mongo;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document("canvas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Canvas {

    @Id
    private String id;

    @Field(name = "canvas_title")
    private String title;

    @Field(name = "canvas_content")
    private String content;

    @Field(name = "workspace_id")
    private Long workSpaceId;

    @Field(name = "create_member_id")
    private Long createMemberId;

    @Field(name = "create_member_name")
    private String createMemberName;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Canvas(String title, String content, Long workSpaceId, Long createMemberId, String createMemberName, LocalDateTime createdAt) {
        this.title = title;
        this.content = content;
        this.workSpaceId = workSpaceId;
        this.createMemberId = createMemberId;
        this.createMemberName = createMemberName;
        this.createdAt = createdAt;
    }


    public static Canvas createCanvas(String title, String content, Long workSpaceId, Long createMemberId, String createMemberName, LocalDateTime createdAt) {
        return Canvas.builder()
                .title(title)
                .content(content)
                .workSpaceId(workSpaceId)
                .createMemberId(createMemberId)
                .createMemberName(createMemberName)
                .createdAt(createdAt)
                .build();
    }
}
