package com.dolphin.demo.dto.request;

import lombok.Getter;


@Getter
public class PlaceRequestDto {
    private String title;
    private String content;
    private String theme;
    private String address;
    private String sigunguCode;
    private String areaCode;
}
