package org.project.nuwabackend.domain.workspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.BaseTimeEntity;
import org.project.nuwabackend.domain.member.Member;

import static jakarta.persistence.FetchType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WorkSpaceMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_member_id")
    private Long id;

    @Column(name = "workspace_member_name")
    private String name;

    @Column(name = "workpsace_member_job")
    private String job;

    @Column(name = "workspace_member_image")
    private String image;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    public WorkSpaceMember(String name, String job, String image, Member member, WorkSpace workSpace) {
        this.name = name;
        this.job = job;
        this.image = image;
        this.member = member;
        this.workSpace = workSpace;
    }
}
