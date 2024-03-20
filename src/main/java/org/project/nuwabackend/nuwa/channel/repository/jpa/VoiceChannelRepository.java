package org.project.nuwabackend.nuwa.channel.repository.jpa;

import org.project.nuwabackend.nuwa.domain.channel.Voice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceChannelRepository extends JpaRepository<Voice, Long> {
}
