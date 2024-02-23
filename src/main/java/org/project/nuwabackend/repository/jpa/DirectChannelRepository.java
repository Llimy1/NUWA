package org.project.nuwabackend.repository.jpa;

import org.project.nuwabackend.domain.channel.Direct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;


public interface DirectChannelRepository extends JpaRepository<Direct, Long> {

    @Query("SELECT d " +
            "FROM Direct d " +
            "JOIN d.createMember cm " +
            "JOIN d.joinMember jm " +
            "WHERE cm.id = :workSpaceMemberId OR jm.id = :workSpaceMemberId")
    Slice<Direct> findDirectChannelByCreateMemberIdOrJoinMemberId(@Param("workSpaceMemberId") Long workSpaceMemberId, Pageable pageable);
}
