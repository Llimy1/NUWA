package org.project.nuwabackend.nuwa.domain.channel;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("chat")
public class Chat extends Channel{

    @Column(name = "chat_member_count")
    private Integer chatMemberCount;

    @Builder
    private Chat(String name, WorkSpace workSpace, WorkSpaceMember createMember, Boolean isCreateMemberDelete, Integer chatMemberCount) {
        super(name, workSpace, createMember, isCreateMemberDelete);
        this.chatMemberCount = chatMemberCount;
    }

    // 채팅 채널 생성
    public static Chat createChatChannel(String name, WorkSpace workSpace, WorkSpaceMember createMember) {
        return Chat.builder()
                .name(name)
                .workSpace(workSpace)
                .createMember(createMember)
                .isCreateMemberDelete(false)
                .chatMemberCount(1)
                .build();
    }

    public void increaseChatMemberCount(int joinMemberCount) {
        this.chatMemberCount = chatMemberCount + joinMemberCount;
    }

    public void decreaseChatMemberCount() {
        this.chatMemberCount--;
    }
}
