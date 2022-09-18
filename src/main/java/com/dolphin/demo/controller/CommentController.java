package com.dolphin.demo.controller;


import com.dolphin.demo.dto.request.CommentRequestDto;
import com.dolphin.demo.dto.response.CommentResponseDto;
import com.dolphin.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    /* 후기 조회 */
    @GetMapping("/comment/{place_id}")
    public ResponseEntity<List<CommentResponseDto>> getComment(@PathVariable Long place_id) {

        return commentService.getComment(place_id);
    }

    /* 후기 작성 */
    @PostMapping("/comment/{place_id}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long place_id,
                                                            @ModelAttribute CommentRequestDto commentRequestDto) throws IOException {

        return commentService.createComment(place_id, commentRequestDto);
    }

}
