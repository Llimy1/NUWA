package org.project.nuwabackend.domain.channel;

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
    @JoinColumn(name = "create_direct_member_id")
    private WorkSpaceMember createMember;

    @ManyToOne
    @JoinColumn(name = "join_direct_member_id")
    private WorkSpaceMember joinMember;

    @Builder
    private Direct(String name, WorkSpace workSpace, WorkSpaceMember createMember, WorkSpaceMember joinMember) {
        super(name, workSpace);
        this.createMember = createMember;
        this.joinMember = joinMember;
    }

    // 다이렉트 채널 생성
    public static Direct createDirectChannel(WorkSpace workSpace, WorkSpaceMember createMember, WorkSpaceMember joinMember) {
        return Direct.builder()
                .name(null)
                .workSpace(workSpace)
                .createMember(createMember)
                .joinMember(joinMember)
                .build();
    }
}
