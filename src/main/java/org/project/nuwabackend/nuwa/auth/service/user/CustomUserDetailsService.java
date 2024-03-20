package org.project.nuwabackend.nuwa.auth.service.user;

import lombok.RequiredArgsConstructor;
import org.project.nuwabackend.nuwa.domain.member.CustomMemberDetails;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.auth.repository.jpa.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.project.nuwabackend.global.response.type.ErrorMessage.EMAIL_NOT_FOUND_ID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(EMAIL_NOT_FOUND_ID));

        return CustomMemberDetails.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRoleKey())
                .build();
    }
}
