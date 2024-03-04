package org.project.nuwabackend.domain.multimedia;

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
import org.project.nuwabackend.domain.base.BaseTimeJpa;
import org.project.nuwabackend.domain.channel.Channel;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.type.FileType;
import org.project.nuwabackend.type.FileUploadType;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class File extends BaseTimeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "file_url", length = 1000)
    private String url;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_extension")
    private String fileExtension;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_upload_type")
    private FileUploadType fileUploadType;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_member_id")
    private WorkSpaceMember workSpaceMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder
    private File(String url, String fileName, Long fileSize, String fileExtension, FileUploadType fileUploadType, FileType fileType, WorkSpaceMember workSpaceMember, WorkSpace workSpace, Channel channel) {
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.fileUploadType = fileUploadType;
        this.fileType = fileType;
        this.workSpaceMember = workSpaceMember;
        this.workSpace = workSpace;
        this.channel = channel;
    }

    // 일반 파일 업로드
    public static File createFile(String url, String fileName, Long fileSize, String fileExtension, FileUploadType fileUploadType, FileType fileType, WorkSpaceMember workSpaceMember, WorkSpace workSpace) {
        return File.builder()
                .url(url)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileExtension(fileExtension)
                .fileUploadType(fileUploadType)
                .fileType(fileType)
                .workSpaceMember(workSpaceMember)
                .workSpace(workSpace)
                .build();
    }
    // 채널 파일 업로드
    public static File createChannelFile(String url, String fileName, Long fileSize, String fileExtension, FileUploadType fileUploadType, FileType fileType, WorkSpaceMember workSpaceMember, WorkSpace workSpace, Channel channel) {
        return File.builder()
                .url(url)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileExtension(fileExtension)
                .fileUploadType(fileUploadType)
                .fileType(fileType)
                .workSpaceMember(workSpaceMember)
                .workSpace(workSpace)
                .channel(channel)
                .build();
    }
}
