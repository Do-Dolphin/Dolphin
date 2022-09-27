package com.dolphin.demo.dto.request;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    @Size(max = 20)
    @NotBlank(message = "제목은 1자 이상 20자 미만으로 작성해주세요.")
    private String title;

    @Size(min = 10, max = 300)
    @NotBlank(message = "리뷰는 10자 이상 300자 미만으로 작성해주세요.")
    private String content;

    @NotNull
    private int star;

}
