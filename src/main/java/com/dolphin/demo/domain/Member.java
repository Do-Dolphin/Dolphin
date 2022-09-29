package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.NicknameDto;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
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

}
