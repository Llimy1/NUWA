package org.project.nuwabackend.nuwa.domain.workspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.nuwa.domain.base.BaseTimeJpa;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;

import static jakarta.persistence.FetchType.*;
import static org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType.CREATED;
import static org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType.JOIN;

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

    @Column(name = "workspace_member_status")
    private String status;

    @Column(name = "workspace_member_type")
    @Enumerated(EnumType.STRING)
    private WorkSpaceMemberType workSpaceMemberType;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @Builder
    public WorkSpaceMember(String name, String job, String image, String status, WorkSpaceMemberType workSpaceMemberType, Boolean isDelete, Member member, WorkSpace workSpace) {
        this.name = name;
        this.job = job;
        this.image = image;
        this.status = status;
        this.workSpaceMemberType = workSpaceMemberType;
        this.isDelete = isDelete;
        this.member = member;
        this.workSpace = workSpace;
    }

    // 워크스페이스 멤버 생성
    public static WorkSpaceMember createWorkSpaceMember(String name, String job, String image, WorkSpaceMemberType workSpaceMemberType, Member member, WorkSpace workSpace) {
        return WorkSpaceMember.builder()
                .name(name)
                .job(job)
                .image(image)
                .workSpaceMemberType(workSpaceMemberType)
                .isDelete(false)
                .member(member)
                .workSpace(workSpace)
                .build();
    }

    // 워크스페이스 멤버 가입
    public static WorkSpaceMember joinWorkSpaceMember(String name, String image, WorkSpaceMemberType workSpaceMemberType, Member member, WorkSpace workSpace) {
        return WorkSpaceMember.builder()
                .name(name)
                .image(image)
                .workSpaceMemberType(workSpaceMemberType)
                .isDelete(false)
                .member(member)
                .workSpace(workSpace)
                .build();
    }

    // 워크스페이스 멤버 정보 편집
    public void updateWorkSpaceMember(String name, String job, String image) {
        this.name = name;
        this.job = job;
        this.image = image;
    }

    // 워크스페이스 상태 편집
    public void updateWorkSpaceMemberStatus(String status) {
        this.status = status;
    }

    // 워크스페이스 멤버 타입 변경 -> 생성한 인원으로
    public void updateCreateWorkSpaceMemberType() {
        this.workSpaceMemberType = CREATED;
    }

    // 워크스페이스 멤버 타입 변경 -> 참여한 인원으로
    public void updateJoinWorkSpaceMemberType() {
        this.workSpaceMemberType = JOIN;
    }

    // 나가기 -> true
    public void deleteWorkSpaceMember() {
        this.isDelete = true;
    }

    // 재참가 -> false
    public void reJoinWorkSpaceMember() {
        this.isDelete = false;
    }
}
