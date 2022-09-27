package com.dolphin.demo.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PlaceRequestDto {
    private String title;
    private String content;
    private String theme;
    private String address;
    private String mapX;
    private String mapY;
    private String sigunguCode;
    private String areaCode;
}
