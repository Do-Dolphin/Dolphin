package com.dolphin.demo.dto.request;


import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class EventRequestDto {

    private String title;
    private String linkUrl;
    private String imageUrl;
    private String period;

}
