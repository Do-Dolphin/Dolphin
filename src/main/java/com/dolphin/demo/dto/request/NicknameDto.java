package com.dolphin.demo.dto.request;

import lombok.Getter;

@Getter
public class NicknameDto {

    private String nickname;
    private String password;
    private String newPassword;
    private String newPasswordConfirm;
}
