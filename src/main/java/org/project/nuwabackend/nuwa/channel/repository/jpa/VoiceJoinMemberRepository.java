package org.project.nuwabackend.nuwa.channel.repository.jpa;

import org.project.nuwabackend.nuwa.domain.channel.VoiceJoinMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceJoinMemberRepository extends JpaRepository<VoiceJoinMember, Long> {
}
