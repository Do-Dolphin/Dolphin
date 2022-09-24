package com.dolphin.demo.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.Image;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.CommentRequestDto;
import com.dolphin.demo.dto.response.CommentResponseDto;
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


    /* ======여행지 상세페이지 후기 조회 ====== */
    public ResponseEntity<List<CommentResponseDto>> getComment(Long place_id) {

        /* 예외처리 추가 예정 */

        /* 해당 여행지의 모든 후기를 작성일 기준 최신순으로 불러옴 */ /* 수정 예정 */
        List<Comment> commentList = commentRepository.findAllByPlaceId(place_id);


        /* 보여줄 데이터 */
        List<CommentResponseDto> commentResult = new ArrayList<>();
        /* 불러온 모든 후기와 후기별 이미지를 comments에 담기 */
        for (Comment comments : commentList) {
            /* 후기별 이미지 리스트 꺼내오기 */
            List<Image> imageResult = imageRepository.findAllByCommentId(comments.getId());
            List<String> imageList = new ArrayList<>();
            /* 꺼내온 이미지들을 imageList에 담기 */

            for(Image images : imageResult) {
                imageList.add(images.getImageUrl());
            }

            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                                    .comment_id(comments.getId())
                                    .place_id(comments.getPlace().getId())
                                    .title(comments.getTitle())
                                    .content(comments.getContent())
                                    .imageList(imageList)
                                    .star(comments.getStar())
                                    .createdAt(comments.getCreatedAt())
                                    .modifiedAt(comments.getModifiedAt())
                                    .build();
            commentResult.add(commentResponseDto);
        }

        return ResponseEntity.ok().body(commentResult);
    }


    /* ====== 후기 등록 ====== */
    public ResponseEntity<CommentResponseDto> createComment(Long place_id, CommentRequestDto commentRequestDto, List<MultipartFile> multipartFile) throws IOException {

        /* 예외처리 추가 예정 */
        /* 여행지 존재 여부 검증 */
        Place place = placeRepository.findById(place_id)
                .orElseThrow(() -> new IllegalArgumentException("여행지가 존재하지 않습니다."));

        /* 제목, 내용, 별점을 저장 */
        Comment comment = new Comment(commentRequestDto, place);
        commentRepository.save(comment);


        /* 이미지 등록하기 */

        /**
         *  이미지 등록하는 경우 : checkNum = 1
         *  이미지 등록하지 않는 경우 : checkNum = 0
         */

        List<String> filenameList;
        List<String> imageList = new ArrayList<>();

        if(multipartFile.get(0).isEmpty()) {
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

            /* 파일을 업로드 후 url과 filename을 리스트로 저장 */
            filenameList = amazonS3Service.upload(multipartFile);
            List<Image> saveImages = new ArrayList<>();
            for (String filename : filenameList) {
                Image image = Image.builder()
                        .comment(comment)
                        .place(place)
                        .filename(filename)
                        .imageUrl(amazonS3Client.getUrl(bucket, filename).toString())
                        .build();

                saveImages.add(image);
                imageList.add(image.getImageUrl());
            }
            imageRepository.saveAll(saveImages);


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



    /* ====== 후기 수정하기 ====== */
    public ResponseEntity<CommentResponseDto> updateComment(Long comment_id, CommentRequestDto commentRequestDto, List<MultipartFile> multipartFile) throws IOException {

        /* 예외처리 추가 예정 */ /* 작성자가 맞는지 */


        /* 후기 존재 여부 검증 */
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new IllegalArgumentException("후기가 존재하지 않습니다."));


        /* 해당 후기의 모든 이미지 불러오기 */
        List<Image> image = imageRepository.findAllByCommentId(comment_id);


        /* 수정된 내용 저장 */
        CommentRequestDto updateComment = CommentRequestDto.builder()
                .title(commentRequestDto.getTitle())
                .content(commentRequestDto.getContent())
                .star(commentRequestDto.getStar())
                .build();
        comment.update(updateComment);
        commentRepository.save(comment);

        /* 이미지 수정 및 재등록 기능 */
        String imageUrl;
        String filename;
        List<String> imageList = new ArrayList<>();

        /**
         *  이미지 등록하는 경우 : checkNum = 1
         *  이미지 등록하지 않는 경우 : checkNum = 0
         */
        int checkNum = 1;
        for(MultipartFile file : multipartFile) {
            if(file.isEmpty()) checkNum = 0;
        }

        if(checkNum == 0) {
            /* 기존 이미지가 있다면 기존 파일 유지하고 없다면 null 유지하기 */
            for (Image existImage : image) {
                imageUrl = existImage.getImageUrl();
                filename = existImage.getFilename();
                Image existImages = Image.builder()
                        .comment(comment)
                        .place(comment.getPlace())
                        .imageUrl(imageUrl)
                        .filename(filename)
                        .build();
                imageRepository.save(existImages);
            }

            for(Image images : image) {
                imageUrl = images.getImageUrl();

                String String = new String(imageUrl);
                imageList.add(String);

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

            /* 새로 등록하는 이미지가 있는 경우 */
        } else {
            /* S3 저장소에 있는 이미지 삭제하기 */
            for (int i=0; i<image.size(); i++) {
                amazonS3Service.deleteFile(image.get(i).getFilename());
            }

            /* DB 이미지 삭제 */
            imageRepository.deleteAll(image);

            /* 새로운 이미지 등록 */
            List<String> filenameList;
            filenameList = amazonS3Service.upload(multipartFile);
            for (String filenames : filenameList) {
                Image images = Image.builder()
                        .comment(comment)
                        .place(comment.getPlace())
                        .filename(filenames)
                        .imageUrl(amazonS3Client.getUrl(bucket, filenames).toString())
                        .build();

                imageRepository.save(images);
            }
            /* 업데이트 된 이미지 목록 불러오기 */
            List<Image> imageResult = imageRepository.findAllByCommentId(comment_id);

            for(Image images : imageResult) {
                imageUrl = images.getImageUrl();

                String String = new String(imageUrl);
                imageList.add(String);
            }
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


   /* ====== 후기 삭제하기 ====== */
    public ResponseEntity<Long> deleteComment(Long id) throws IOException {

        /* 예외처리 추가 예정 */
        /* 작성자가 맞는지 */

        /* 후기 존재 여부 검증 */
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("후기가 존재하지 않습니다."));

        List<Image> image = imageRepository.findAllByCommentId(id);

        /* 저장된 이미지가 있으면 S3 저장소에 있는 이미지 삭제하기 */
        for(int i=0; i<image.size(); i++) {
            amazonS3Service.deleteFile(image.get(i).getFilename());
        }

        /* 후기 삭제 */
        commentRepository.delete(comment);

        return ResponseEntity.ok().body(id);
    }

}
