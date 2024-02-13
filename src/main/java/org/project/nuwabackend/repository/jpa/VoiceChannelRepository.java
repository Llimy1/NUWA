package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Voice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceChannelRepository extends JpaRepository<Voice, Long> {
}
