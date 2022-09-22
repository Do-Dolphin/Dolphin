package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PlaceListResponseDto {
    private Long id;
    private String title;
    private float star;
    private String image;
    private String theme;
}
