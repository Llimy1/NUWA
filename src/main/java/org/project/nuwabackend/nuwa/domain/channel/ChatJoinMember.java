package org.project.nuwabackend.nuwa.domain.channel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatJoinMember {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "chat_join_member_id")
    private Long id;

    @Column(name = "is_join_member_delete")
    private Boolean isJoinMemberDelete;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_join_workspace_member_id")
    private WorkSpaceMember joinMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_channel_id")
    private Chat chatChannel;

    @Builder
    private ChatJoinMember(Boolean isJoinMemberDelete, WorkSpaceMember joinMember, Chat chatChannel) {
        this.isJoinMemberDelete = isJoinMemberDelete;
        this.joinMember = joinMember;
        this.chatChannel = chatChannel;
    }

    // 참여 멤버 생성
    public static ChatJoinMember createChatJoinMember(WorkSpaceMember joinMember, Chat chatChannel) {
        return ChatJoinMember.builder()
                .isJoinMemberDelete(false)
                .joinMember(joinMember)
                .chatChannel(chatChannel)
                .build();
    }

    // 참여한 인원 채팅 채널 삭제 -> true
    public void deleteJoinMember() {
        this.isJoinMemberDelete = true;
    }

    // 참여한 인원 채팅 채널 복구 -> false
    public void restoreJoinMember() {
        this.isJoinMemberDelete = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatJoinMember that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
