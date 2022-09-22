package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RandomPlaceResponseDto {
    private String area;
    private List<PlaceListResponseDto> placeList;
}
