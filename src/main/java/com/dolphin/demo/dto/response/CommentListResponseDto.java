package com.dolphin.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponseDto {

    private Long comment_id;
    private Long place_id;
    private String placeTitle;
    private String title;
    private LocalDateTime createdAt;
    private boolean image;

}