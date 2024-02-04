package org.project.nuwabackend.domain.channel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import org.project.nuwabackend.type.ChannelType;

import static jakarta.persistence.FetchType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChannelMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_member_id")
    private Long id;

    @Column(name = "channel_member_type")
    private ChannelType channelType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public ChannelMember(ChannelType channelType, Member member, Channel channel) {
        this.channelType = channelType;
        this.member = member;
        this.channel = channel;
    }
}
