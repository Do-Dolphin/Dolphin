package com.dolphin.demo.dto.response;


import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class EventResponseDto {

    private Long id;
    private String title;
    private String linkUrl; // 행사 링크
    private String imageUrl;
    private String period; // 행사기간

}
