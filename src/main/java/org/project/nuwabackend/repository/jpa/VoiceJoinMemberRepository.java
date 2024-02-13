package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.VoiceJoinMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceJoinMemberRepository extends JpaRepository<VoiceJoinMember, Long> {
}
