package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.NicknameDto;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role;

    public void updateNickname(NicknameDto nicknameDto){
        this.nickname = nicknameDto.getNickname();
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }

}
