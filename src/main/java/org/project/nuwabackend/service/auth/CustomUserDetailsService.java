package org.project.nuwabackend.service.auth;

import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.domain.member.CustomMemberDetails;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.type.ErrorMessage.EMAIL_NOT_FOUND_MEMBER;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(EMAIL_NOT_FOUND_MEMBER));

        return CustomMemberDetails.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRoleKey())
                .build();
    }
}
