package com.dolphin.demo.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;


@Builder
@Getter
public class EventResponseDto {

    private Long id;
    private String title;
    private String linkUrl; // 행사 링크
    private String imageUrl;
    private String period; // 행사기간

}
