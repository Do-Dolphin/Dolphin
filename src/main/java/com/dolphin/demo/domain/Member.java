package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.NicknameDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void updateNickname(NicknameDto nicknameDto) {
        this.nickname = nicknameDto.getNickname();
    }


}
