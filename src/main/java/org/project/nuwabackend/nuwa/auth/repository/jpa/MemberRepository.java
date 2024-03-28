package org.project.nuwabackend.nuwa.auth.repository.jpa;

import org.project.nuwabackend.nuwa.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmail(String email);
}
