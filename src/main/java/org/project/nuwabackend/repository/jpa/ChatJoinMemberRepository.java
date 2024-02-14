package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.ChatJoinMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatJoinMemberRepository extends JpaRepository<ChatJoinMember, Long> {
}
