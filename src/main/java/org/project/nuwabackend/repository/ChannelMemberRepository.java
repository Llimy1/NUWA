package org.project.nuwabackend.repository;

import org.project.nuwabackend.domain.channel.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
}
