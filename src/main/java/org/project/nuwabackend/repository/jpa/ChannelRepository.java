package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Channel;
import org.project.nuwabackend.domain.channel.Direct;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

}
