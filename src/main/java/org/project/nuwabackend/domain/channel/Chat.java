package org.project.nuwabackend.domain.channel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.jdbc.Work;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("chat")
public class Chat extends Channel{

    @ManyToOne
    @JoinColumn(name = "create_chat_member_id")
    private WorkSpaceMember createMember;

    @Builder
    private Chat(String name, WorkSpace workSpace, WorkSpaceMember createMember) {
        super(name, workSpace);
        this.createMember = createMember;
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
