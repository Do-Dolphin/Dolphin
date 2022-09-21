package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PlaceResponseDto {
    private Long id;
    private String title;
    private String content;
    private List<String> imageUrl;
    private float star;
    private int likes;
    private String theme;
    private String address;
    private String mapX;
    private String mapY;
}
