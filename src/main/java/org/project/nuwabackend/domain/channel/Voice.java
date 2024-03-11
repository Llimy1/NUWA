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
@DiscriminatorValue("voice")
public class Voice extends Channel {

    @Builder
    private Voice(String name, WorkSpace workSpace, WorkSpaceMember createMember) {
        super(name, workSpace, createMember);
    }

    public static Voice createVoiceChannel(String name, WorkSpace workSpace, WorkSpaceMember createMember) {
        return Voice.builder()
                .name(name)
                .workSpace(workSpace)
                .createMember(createMember)
                .build();
    }
}
