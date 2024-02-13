package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Direct;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;


public  interface DirectChannelRepository extends JpaRepository<Direct, Long> {

    Slice<Direct> findDirectChannelByWorkSpaceId(Long WorkSpaceId);
}
