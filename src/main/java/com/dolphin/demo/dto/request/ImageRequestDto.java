package com.dolphin.demo.dto.request;

import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.Place;
import lombok.*;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ImageRequestDto {

    private String imageUrl;
    private String filename;
    private List<String> imageUrlList;
    private List<String> filenameList;
    private Comment comment;
    private Place place;

}
