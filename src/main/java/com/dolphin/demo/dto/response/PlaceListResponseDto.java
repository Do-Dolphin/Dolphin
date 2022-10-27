package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaceListResponseDto {
    private Long id;
    private String title;
    private float star;
    private String image;
    private String theme;
    private boolean state;
}
