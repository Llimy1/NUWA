package org.project.nuwabackend.domain.member;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.auth.type.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Member Domain Test")
class MemberTest {

    String email = "email";
    String password = "password";
    String nickname = "nickname";
    String phoneNumber = "phoneNumber";
    String provider = "provider";

    @Test
    @DisplayName("[Domain] Create Member")
    void createMemberTest() {
        //given
        //when
        Member member = Member.createMember(email, password, nickname, phoneNumber);

        //then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(member.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("[Domain] Create Social Member")
    void createSocialMemberTest() {
        //given
        //when
        Member member = Member.createSocialMember(email, nickname, phoneNumber, provider);

        //then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(member.getRole()).isEqualTo(Role.USER);
        assertThat(member.getProvider()).isEqualTo(provider);
    }


    @Test
    @DisplayName("[Domain] Password Encoder")
    void passwordEncoder() {
        //given
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "password";

        //when
        String encodePassword = passwordEncoder.encode(password);

        //then
        assertThat(passwordEncoder.matches(password, encodePassword)).isTrue();
    }
    @Test
    @DisplayName("[Domain] Role Key Return")
    void roleKeyReturn() {
        //given
        Member member = Member.createMember(email, password, nickname, phoneNumber);

        //when
        String role = member.getRoleKey();

        //then
        assertThat(role).isEqualTo(member.getRole().getKey());
    }
}