package com.dolphin.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    @Size(max = 20)
    @NotBlank(message = "제목은 1자 이상 20자 미만으로 작성해주세요.")
    private String title;

    @Size(min = 10, max = 300)
    @NotBlank(message = "리뷰는 10자 이상 300자 미만으로 작성해주세요.")
    private String content;

    @NotBlank
    private int star;

}
