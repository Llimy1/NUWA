package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.ChatJoinMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatJoinMemberRepository extends JpaRepository<ChatJoinMember, Long> {

    List<ChatJoinMember> findByChatChannelId(Long chatChannelId);


}
