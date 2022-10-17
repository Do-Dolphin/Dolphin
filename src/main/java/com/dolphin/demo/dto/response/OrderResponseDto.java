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
public class OrderResponseDto {

    private Long id;
    private Long place_id;
    private String type;
    private String nickname;
    private String username;
    private String title;
    private String content;
    private boolean state;
    private String answer;
    private List<String> imageList;
    private LocalDateTime createdAt;

}
