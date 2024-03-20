package org.project.nuwabackend.nuwa.domain.channel;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.nuwa.domain.base.BaseTimeJpa;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;

import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn // 하위 테이블의 구분 컬럼 생성(default = DTYPE)
@Entity
public abstract class Channel extends BaseTimeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Long id;

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "room_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "create_member_id")
    private WorkSpaceMember createMember;

    @Column(name = "is_create_member_delete")
    private Boolean isCreateMemberDelete;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    protected Channel(String name, WorkSpace workSpace, WorkSpaceMember workSpaceMember, Boolean isCreateMemberDelete) {
        this.roomId = UUID.randomUUID().toString();
        this.name = name;
        this.workSpace = workSpace;
        this.createMember = workSpaceMember;
        this.isCreateMemberDelete = isCreateMemberDelete;
    }

    // 생성한 인원 채널 삭제 -> true
    public void deleteCreateMember() {
        this.isCreateMemberDelete = true;
    }

    // 생성한 인원 채널 복구 -> false
    public void restoreCreateMember() {
        this.isCreateMemberDelete = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel channel)) return false;
        return Objects.equals(id, channel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
