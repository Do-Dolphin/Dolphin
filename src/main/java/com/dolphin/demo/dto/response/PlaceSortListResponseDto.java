package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaceSortListResponseDto {
    private Long id;
    private String title;
    private float star;
    private String image;
    private boolean state;
    private Long readCount;
    private int commentCount;
}
