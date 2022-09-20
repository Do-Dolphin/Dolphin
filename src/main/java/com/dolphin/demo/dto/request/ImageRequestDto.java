package com.dolphin.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ImageRequestDto {

    private String imageUrl;
    private String filename;

}
