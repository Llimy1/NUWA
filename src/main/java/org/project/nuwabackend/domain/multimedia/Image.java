package org.project.nuwabackend.domain.multimedia;

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
import org.project.nuwabackend.domain.channel.Channel;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;

import static jakarta.persistence.FetchType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Image extends BaseTimeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "image_url")
    private String url;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder
    public Image(String url, Member member, WorkSpace workSpace, Channel channel) {
        this.url = url;
        this.member = member;
        this.workSpace = workSpace;
        this.channel = channel;
    }

    // TODO: test code
    public static Image createImage(String url, Member member, WorkSpace workSpace, Channel channel) {
        return Image.builder()
                .url(url)
                .member(member)
                .workSpace(workSpace)
                .channel(channel)
                .build();
    }
}
