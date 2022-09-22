package com.dolphin.demo.dto.request;

import com.dolphin.demo.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    private String title;
    private String content;
    private int star;

}
