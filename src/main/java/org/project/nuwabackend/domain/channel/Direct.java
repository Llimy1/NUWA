package org.project.nuwabackend.domain.channel;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("direct")
public class Direct extends Channel {

    @ManyToOne
    @JoinColumn(name = "join_direct_member_id")
    private WorkSpaceMember joinMember;

    @Column(name = "is_join_member_delete")
    private Boolean isJoinMemberDelete;

    @Builder
    private Direct(String name, WorkSpace workSpace, WorkSpaceMember createMember, WorkSpaceMember joinMember, Boolean isCreateMemberDelete, Boolean isJoinMemberDelete) {
        super(name, workSpace, createMember, isCreateMemberDelete);
        this.joinMember = joinMember;
        this.isJoinMemberDelete = isJoinMemberDelete;
    }

    // 다이렉트 채널 생성
    public static Direct createDirectChannel(WorkSpace workSpace, WorkSpaceMember createMember, WorkSpaceMember joinMember) {
        return Direct.builder()
                .name(null)
                .workSpace(workSpace)
                .createMember(createMember)
                .isCreateMemberDelete(false)
                .joinMember(joinMember)
                .isJoinMemberDelete(false)
                .build();
    }

    // 참여한 인원 다이렉트 채널 삭제 -> true
    public void deleteJoinMember() {
        this.isJoinMemberDelete = true;
    }

    // 참여한 인원 다이렉트 채널 복구 -> false
    public void restoreJoinMember() {
        this.isJoinMemberDelete = false;
    }
}
