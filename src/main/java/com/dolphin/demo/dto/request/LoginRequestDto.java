package com.dolphin.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
@Getter
@Setter
public class LoginRequestDto {

    @NotNull(message = "아이디는 공백이 될수 없습니다.")
    @Pattern(regexp = "\\w+@\\w+\\.\\w+(\\.\\w+)?", message = "아이디는 이메일 형식이여야 합니다.")
    private String username;

    private String password;
}
