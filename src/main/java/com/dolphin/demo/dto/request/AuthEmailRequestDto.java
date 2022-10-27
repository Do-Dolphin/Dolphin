package com.dolphin.demo.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
public class AuthEmailRequestDto {

    private String email;
    private String code;
}
