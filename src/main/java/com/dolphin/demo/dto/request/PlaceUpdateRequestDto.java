package com.dolphin.demo.dto.request;

import lombok.Getter;
import java.util.List;


@Getter
public class PlaceUpdateRequestDto {
    private String title;
    private String content;
    private String theme;
    private String address;
    private String mapX;
    private String mapY;
    private String sigunguCode;
    private String areaCode;

    private List<String> existUrlList;
}
