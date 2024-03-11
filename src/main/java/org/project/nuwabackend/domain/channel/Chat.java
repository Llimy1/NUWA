package org.project.nuwabackend.domain.channel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("chat")
public class Chat extends Channel{

    @Builder
    private Chat(String name, WorkSpace workSpace, WorkSpaceMember createMember) {
        super(name, workSpace, createMember);
    }

    // 채팅 채널 생성
    public static Chat createChatChannel(String name, WorkSpace workSpace, WorkSpaceMember createMember) {
        return Chat.builder()
                .name(name)
                .workSpace(workSpace)
                .createMember(createMember)
                .build();
    }
}
