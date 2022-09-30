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
    private String areaCode;
    private String linkUrl; // 행사 링크
    private String imageUrl;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd", timezone = "Asia/Seoul")
    private String period; // 행사기간



}
