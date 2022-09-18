package com.dolphin.demo.dto.request;

import com.dolphin.demo.domain.ImageMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    private String title;
    private String comment;
    private ArrayList<ImageMapper> imageList;

}
