package org.project.nuwabackend.nuwa.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.nuwa.domain.base.BaseTimeJpa;
import org.project.nuwabackend.nuwa.auth.type.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    private Member(String email, String password, String nickname, String phoneNumber, String provider, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.provider = provider;
        this.role = role;
    }

    // 멤버 생성
    public static Member createMember(String email, String password, String nickname, String phoneNumber) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();
    }

    // 소셜 멤버 생성
    public static Member createSocialMember(String email, String nickname, String phoneNumber, String provider) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .provider(provider)
                .role(Role.USER)
                .build();
    }

    // 비밀번호 변경
    public void passwordChange(String password) {
        this.password = password;
    }

    // 제공자 업데이트
    public void updateProvider(String provider) {
        this.provider = provider;
    }

    // 비밀번호 암호화
    public void passwordEncoder(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    // Role Key 반환
    public String getRoleKey() {
        return role.getKey();
    }
}
