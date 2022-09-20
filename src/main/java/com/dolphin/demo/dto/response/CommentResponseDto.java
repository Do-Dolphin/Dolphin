package com.dolphin.demo.dto.response;

import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.Image;
import com.dolphin.demo.dto.request.ImageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long comment_id;
    private Long place_id;
//    private Long member_id;
    private String title;
    private String content;
    private List<ImageResponseDto> imageList;
//    private String nickname;
    private int star;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment, List<ImageResponseDto> imageList) {
        this.comment_id = comment.getId();
        this.place_id = comment.getPlace().getId();
        this.title = comment.getTitle();
        this.content = comment.getContent();
        this.imageList = imageList;
        this.star = comment.getPlace().getStar();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }

}
