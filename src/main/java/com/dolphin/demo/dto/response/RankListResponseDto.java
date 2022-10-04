package com.dolphin.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RankListResponseDto {
    private List<PlaceSortListResponseDto> foodList;
    private List<PlaceSortListResponseDto> tourList;
    private List<PlaceSortListResponseDto> activityList;
    private List<PlaceSortListResponseDto> museumList;
}
