package com.dolphin.demo.dto.request;


import lombok.Getter;

@Getter
public class KakaoUserInfoDto {

    private Long kakaoId;

    private String email;

    private String nickname;

    public KakaoUserInfoDto(Long KakaoId, String email, String nickname) {
        this.kakaoId = KakaoId;
        this.email = email;
        this.nickname = nickname;
    }

}
