package com.dolphin.demo.dto.request;

import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.CommentImage;
import com.dolphin.demo.domain.Place;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CommentImageRequestDto {

    private String imageUrl;
    private List<CommentImage> imageUrlList;
    private Comment comment;
    private Place place;

}
