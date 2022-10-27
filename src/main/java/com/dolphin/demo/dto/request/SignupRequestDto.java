package com.dolphin.demo.dto.request;

import lombok.Getter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
public class SignupRequestDto {

    @NotNull(message = "아이디는 공백이 될수 없습니다.")
    @Pattern(regexp = "\\w+@\\w+\\.\\w+(\\.\\w+)?", message = "아이디는 이메일 형식이여야 합니다.")
    private String username;

    @NotNull(message = "비밀번호는 공백이 될 수 없습니다.")
    @Pattern(regexp ="^(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}$",
            message = "비밀번호는 영문,숫자 포함 8~20자리여야 합니다")
    private String password;

    @NotNull(message = "비밀번호 확인은 공백이 될 수 없습니다.")
    @Pattern(regexp ="^(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}$",
            message = "비밀번호 확인은 영문,숫자 포함 8~20자리여야 합니다")
    private String passwordConfirm;


}
