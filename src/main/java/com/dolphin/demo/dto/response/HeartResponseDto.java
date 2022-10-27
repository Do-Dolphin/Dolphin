package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class HeartResponseDto {

    private boolean state;
    private int count;
}
