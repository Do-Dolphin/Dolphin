package com.dolphin.demo.dto.request;

import lombok.Getter;

import java.util.List;


@Getter
public class CourseRequestDto {
    private String name;
    private List<CourseDataRequestDto> data;
}
