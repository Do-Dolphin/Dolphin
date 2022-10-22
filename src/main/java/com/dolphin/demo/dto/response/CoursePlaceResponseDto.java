package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CoursePlaceResponseDto {
    private Long id;
    private String title;
    private float star;
    private String adress;
    private String image;
    private String theme;
    private String mapX;
    private String mapY;
}
