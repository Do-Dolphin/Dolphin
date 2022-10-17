package com.dolphin.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateRequestDto {

    private Long id;
    private String username;
    private String answer;

}
