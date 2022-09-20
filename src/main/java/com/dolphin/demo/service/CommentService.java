package com.dolphin.demo.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.Image;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.CommentRequestDto;
import com.dolphin.demo.dto.request.ImageRequestDto;
import com.dolphin.demo.dto.response.CommentResponseDto;
import com.dolphin.demo.dto.response.ImageResponseDto;
import com.dolphin.demo.repository.CommentRepository;
import com.dolphin.demo.repository.ImageRepository;
import com.dolphin.demo.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CommentService {

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final CommentRepository commentRepository;
    private final AmazonS3Service amazonS3Service;
    private final ImageRepository imageRepository;
    private final PlaceRepository placeRepository;


    /* 여행지 상세페이지 후기 조회 */
    public ResponseEntity<List<CommentResponseDto>> getComment(Long place_id) {

        /* 예외처리 추가 예정 */

        /* 해당 여행지의 모든 후기를 작성일 기준 최신순으로 불러옴 */ /* 수정 예정 */
        List<Comment> commentList = commentRepository.findAllByPlaceId(place_id);

        /* 보여줄 데이터 리스트 */
        List<CommentResponseDto> commentResult = new ArrayList<>();
        for (Comment comments : commentList) {
            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                                    .comment_id(comments.getId())
                                    .place_id(comments.getPlace().getId())
                                    .title(comments.getTitle())
                                    .content(comments.getContent())
                                    .star(comments.getStar())
                                    .createdAt(comments.getCreatedAt())
                                    .modifiedAt(comments.getModifiedAt())
                                    .build();
            commentResult.add(commentResponseDto);
        }

        return ResponseEntity.ok().body(commentResult);
    }


    /* 후기 등록 */
    public ResponseEntity<CommentResponseDto> createComment(Long place_id, CommentRequestDto commentRequestDto, List<MultipartFile> multipartFile) throws IOException {

        /* 예외처리 추가 예정 */
        /* 여행지 존재 여부 검증 */
        Place place = placeRepository.findById(place_id)
                .orElseThrow(() -> new IllegalArgumentException("여행지가 존재하지 않습니다."));

        /* 제목, 내용, 별점을 저장 */
        Comment comment = Comment.builder()
                .place(place)
                .title(commentRequestDto.getTitle())
                .content(commentRequestDto.getContent())
                .star(place.getStar())
                .build();

        commentRepository.save(comment);


        /* 이미지 등록하기 */
        /* 파일을 업로드 후 url과 filename을 리스트로 저장 */
        if(multipartFile != null) {

            List<String>filenameList = amazonS3Service.upload(multipartFile);
            for (String filename : filenameList) {
                Image image = Image.builder()
                        .comment(comment)
                        .filename(filename)
                        .imageUrl(amazonS3Client.getUrl(bucket, filename).toString())
                        .build();
                imageRepository.save(image);
            }
        }

        String imageUrl = null;
        String filename = null;

        List<Image> imageResult = imageRepository.findAllByCommentId(comment.getId());
        List<ImageResponseDto> imageList = new ArrayList<>();

        for(Image images : imageResult) {
            imageUrl = images.getImageUrl();
            filename = images.getFilename();

            ImageResponseDto imageResponseDto = new ImageResponseDto(imageUrl, filename);
            imageList.add(imageResponseDto);

        }

        return ResponseEntity.ok().body(CommentResponseDto.builder()
                .comment_id(comment.getId())
                .place_id(comment.getPlace().getId())
                .title(comment.getTitle())
                .content(comment.getContent())
                .imageList(imageList)
                .star(comment.getStar())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build());

   }

//   /* 후기 수정하기 */
//    public ResponseEntity<CommentResponseDto> updateComment(Long comment_id, CommentRequestDto commentRequestDto, List<MultipartFile> multipartFile) throws IOException {
//
//        /* 예외처리 추가 예정 */ /* 작성자가 맞는지 */
//
//        Place place = placeRepository.findByCommentId(comment_id)
//                .orElseThrow(() -> new IllegalArgumentException("후기가 존재하지 않습니다."));
//
//        List<Image> image = imageRepository.findAllByCommentId(comment_id);
//
//
//        /* 제목, 내용, 별점을 저장 */
//        Comment comment = Comment.builder()
//                .place(place)
//                .title(commentRequestDto.getTitle())
//                .content(commentRequestDto.getContent())
//                .star(place.getStar())
//                .build();
//
//        commentRepository.save(comment);
//
//        if(multipartFile != null) {
//            /* 기존 이미지가 존재하는 경우 */
//            if(image.size() > 0) {
//                /* S3 저장소에 있는 이미지 삭제하기 */
//                for (int i = 0; i < image.size(); i++) {
//                    amazonS3Service.deleteFile(image.get(i).getFilename());
//                }
//            }
//
//            List<String> filenameList = amazonS3Service.upload(multipartFile);
//            for (String filename : filenameList) {
//                Image images = Image.builder()
//                        .comment(comment)
//                        .filename(filename)
//                        .imageUrl(amazonS3Client.getUrl(bucket, filename).toString())
//                        .build();
//                imageRepository.save(images);
//            }
//        }
//
//
//        return ResponseEntity.ok().body(CommentResponseDto.builder()
//                .comment_id(comment.getId())
//                .place_id(comment.getPlace().getId())
//                .title(comment.getTitle())
//                .content(comment.getContent())
//                .star(comment.getStar())
//                .createdAt(comment.getCreatedAt())
//                .modifiedAt(comment.getModifiedAt())
//                .build());
//    }


   /* 후기 삭제하기 */
    public ResponseEntity<Long> deleteComment(Long comment_id) throws IOException {

        /* 예외처리 추가 예정 */ /* 작성자가 맞는지 */
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new IllegalArgumentException("후기가 존재하지 않습니다."));

        List<Image> image = imageRepository.findAllByCommentId(comment_id);

        /* 저장된 이미지가 있으면 S3 저장소에 있는 이미지 삭제하기 */
        for(int i=0; i<image.size(); i++) {
            amazonS3Service.deleteFile(image.get(i).getFilename());
        }

        commentRepository.delete(comment);

        return ResponseEntity.ok().body(comment_id);
    }

}
