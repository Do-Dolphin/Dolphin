package com.dolphin.demo.controller;

import com.dolphin.demo.domain.Member;
import com.dolphin.demo.dto.request.CommentRequestDto;
import com.dolphin.demo.dto.response.CommentResponseDto;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.repository.MemberRepository;
import com.dolphin.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    /* 여행지 상세페이지의 후기 전체 조회 */
    @GetMapping("/comment/{place_id}")
    public ResponseEntity<List<CommentResponseDto>> getComment(@PathVariable Long place_id) {

        return commentService.getComment(place_id);
    }

    /* 후기 작성 */
    @PostMapping("/auth/comment/{place_id}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long place_id,
                                                            @RequestPart(value = "data") CommentRequestDto commentRequestDto,
                                                            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFile) throws IOException {


        return commentService.createComment(place_id, commentRequestDto, multipartFile);
    }


    /* 후기 수정 */
    @PutMapping("/auth/comment/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long id,
                                                            @RequestPart(value = "data") CommentRequestDto commentRequestDto,
                                                            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFile) throws IOException {

        return commentService.updateComment(id, commentRequestDto, multipartFile);
    }


    /* 후기 삭제 */
    @DeleteMapping("/auth/comment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) throws IOException {

        commentService.deleteComment(id);

        return ResponseEntity.ok().body("Delete comment_id : " + id);
    }

}
