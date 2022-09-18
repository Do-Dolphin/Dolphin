package com.dolphin.demo.dto.response;

import com.dolphin.demo.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long comment_id;
//    private Long place_id;
//    private Long member_id;
    private String title;
    private String comment;
//    private String nickname;
    private String imageUrl;
//    private String star;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public CommentResponseDto(Comment comment) {
        this.comment_id = comment.getId();
        this.title = comment.getTitle();
        this.comment = comment.getComment();
        this.imageUrl = comment.getImageUrl();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();

    }

}
