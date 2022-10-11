package com.dolphin.demo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class PlaceSearchDto {

    private Long placeId;
    private String title;
    private float star;
    private String image;
    private Long readCount;
    private int commentCount;


    @QueryProjection
    public PlaceSearchDto(Long placeId, String title, float star, String image, Long readCount, int commentCount) {
        this.placeId = placeId;
        this.title = title;
        this.star = star;
        this.image = image;
        this.readCount = readCount;
        this.commentCount = commentCount;
    }
}
