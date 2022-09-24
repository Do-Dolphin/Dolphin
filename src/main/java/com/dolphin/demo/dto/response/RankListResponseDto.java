package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RankListResponseDto {
    private List<PlaceListResponseDto> foodList;
    private List<PlaceListResponseDto> tourList;
    private List<PlaceListResponseDto> activityList;
    private List<PlaceListResponseDto> museumList;
}
