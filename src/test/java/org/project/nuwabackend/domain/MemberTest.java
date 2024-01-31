package org.project.nuwabackend.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.type.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] Member Domain Test")
class MemberTest {

    @Test
    @DisplayName("[Domain] Create Member")
    void createMemberTest() {
        //given
        String email = "email";
        String password = "password";
        String nickname = "nickname";
        String phoneNumber = "phoneNumber";

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


}