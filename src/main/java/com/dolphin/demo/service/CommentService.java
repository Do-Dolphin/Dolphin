package com.dolphin.demo.service;

import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.dto.request.CommentRequestDto;
import com.dolphin.demo.dto.response.CommentResponseDto;
import com.dolphin.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final AmazonS3Service amazonS3Service;


    /* 여행지 상세페이지 리뷰 조회 */
    public ResponseEntity<List<CommentResponseDto>> getComment(Long place_id) {

        /* 예외처리 추가 예정 */

        /* 해당 여행지의 모든 리뷰를 작성일 기준 최신순으로 불러옴 */
        List<Comment> commentList = commentRepository.findAllByPlaceIdByCreatedAtDesc(place_id);

        /* 보여줄 데이터 리스트 */
        List<CommentResponseDto> commentResult = new ArrayList<>();
        for (Comment comments : commentList) {
            CommentResponseDto commentResponseDto = new CommentResponseDto(comments);
            commentResult.add(commentResponseDto);
        }

        return ResponseEntity.ok().body(commentResult);
    }

    /* 리뷰 등록 */
    public ResponseEntity<CommentResponseDto> createComment(Long place_id, CommentRequestDto commentRequestDto) {

        /* 예외처리 추가 예정 */

        /* 이미지 등록하기 */

    }
}
