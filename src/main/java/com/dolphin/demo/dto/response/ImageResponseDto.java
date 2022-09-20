package com.dolphin.demo.dto.response;

import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ImageResponseDto {

    private String imageUrl;
    private String filename;

}
