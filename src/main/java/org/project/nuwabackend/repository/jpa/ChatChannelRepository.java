package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatChannelRepository extends JpaRepository<Chat, Long> {
}
