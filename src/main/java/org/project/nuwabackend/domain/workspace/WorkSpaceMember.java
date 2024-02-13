package org.project.nuwabackend.domain.workspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.base.BaseTimeJpa;
import org.project.nuwabackend.domain.member.Member;

import static jakarta.persistence.FetchType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WorkSpaceMember extends BaseTimeJpa {

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

    @Builder
    private WorkSpaceMember(String name, String job, String image, Member member, WorkSpace workSpace) {
        this.name = name;
        this.job = job;
        this.image = image;
        this.member = member;
        this.workSpace = workSpace;
    }

    // 워크스페이스 멤버 생성
    public static WorkSpaceMember createWorkSpaceMember(String name, String job, String image, Member member, WorkSpace workSpace) {
        return WorkSpaceMember.builder()
                .name(name)
                .job(job)
                .image(image)
                .member(member)
                .workSpace(workSpace)
                .build();
    }
}
