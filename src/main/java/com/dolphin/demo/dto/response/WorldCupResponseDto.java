package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorldCupResponseDto {
    private Long id;
    private String title;
    private String image;
}
