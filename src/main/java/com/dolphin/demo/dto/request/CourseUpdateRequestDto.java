package com.dolphin.demo.dto.request;

import lombok.Getter;

import java.util.List;


@Getter
public class CourseUpdateRequestDto {
    private String name;
    private List<CourseUpdateDataRequestDto> data;
}
