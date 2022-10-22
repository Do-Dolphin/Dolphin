package com.dolphin.demo.service;

import com.dolphin.demo.domain.*;
import com.dolphin.demo.dto.repository.CommentImageRepository;
import com.dolphin.demo.dto.repository.CommentRepository;
import com.dolphin.demo.dto.repository.MemberRepository;
import com.dolphin.demo.dto.repository.PlaceRepository;
import com.dolphin.demo.dto.request.CommentRequestDto;
import com.dolphin.demo.dto.request.ImageRequestDto;
import com.dolphin.demo.dto.response.CommentResponseDto;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
import com.dolphin.demo.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dolphin.demo.exception.ErrorCode.*;


@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AmazonS3Service amazonS3Service;
    private final CommentImageRepository commentImageRepository;
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;


    // 여행지 상세페이지 후기 조회
    public ResponseEntity<List<CommentResponseDto>> getComment(Long place_id) {

        // 해당 여행지의 모든 후기를 불러옴
        List<Comment> commentList = commentRepository.findAllByPlaceId(place_id);


        // 보여줄 데이터
        List<CommentResponseDto> commentResult = new ArrayList<>();
        // 불러온 모든 후기와 후기별 이미지를 comments에 담기
        for (Comment comments : commentList) {
            // 후기별 이미지 리스트 꺼내오기
            List<CommentImage> imageResult = commentImageRepository.findAllByCommentId(comments.getId());
            List<String> imageList = new ArrayList<>();

            // 꺼내온 이미지들을 imageList에 담기
            for(CommentImage images : imageResult) {
                imageList.add(images.getImageUrl());
            }

            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                    .comment_id(comments.getId())
                    .place_id(comments.getPlace().getId())
                    .placeTitle(comments.getPlace().getTitle())
                    .nickname(comments.getMember().getNickname())
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


    // 후기 등록
    public ResponseEntity<CommentResponseDto> createComment(Long place_id, CommentRequestDto commentRequestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {

        // 로그인한 회원인지 여부 검증
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        // 여행지 존재 여부 검증
        Place place = placeRepository.findById(place_id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PLACE));

        // 제목, 내용, 별점을 저장
        Comment comment = new Comment(commentRequestDto, place, member);
        place.updateStar(comment.getStar(),1);
        commentRepository.save(comment);


        // 이미지 등록하기
        List<String> imageUrlList;
        List<String> imageList = new ArrayList<>();

        imageUrlList = amazonS3Service.upload(multipartFile);
        List<CommentImage> saveImages = new ArrayList<>();
        for (String imageUrl : imageUrlList) {
            CommentImage image = CommentImage.builder()
                    .comment(comment)
                    .member(member)
                    .imageUrl(imageUrl)
                    .build();
            saveImages.add(image);
            imageList.add(image.getImageUrl());
        }

        notificationService.send(member, "후기가 성공적으로 저장되었습니다");

        commentImageRepository.saveAll(saveImages);
        return ResponseEntity.ok().body(CommentResponseDto.builder()
                .comment_id(comment.getId())
                .place_id(comment.getPlace().getId())
                .placeTitle(comment.getPlace().getTitle())
                .nickname(comment.getMember().getNickname())
                .title(comment.getTitle())
                .content(comment.getContent())
                .imageList(imageList)
                .star(comment.getStar())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build());
    }


    // 후기 수정하기
    public ResponseEntity<CommentResponseDto> updateComment(Long comment_id, ImageRequestDto imageRequestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {

        // 로그인한 회원인지 여부 검증
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        // 후기 존재 여부 검증
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));

        // 작성자가 맞는지 여부 검증
        if(!(comment.getMember().getId().equals(member.getId()))) {
            throw new CustomException(DO_NOT_MATCH_USER);
        }


        // 해당 후기의 모든 이미지 불러오기
        List<CommentImage> image = commentImageRepository.findAllByCommentId(comment_id);
        comment.getPlace().updateStar(imageRequestDto.getStar()-comment.getStar(),0);

        comment.update(imageRequestDto);
        commentRepository.save(comment);

        // 이미지 수정 및 재등록 기능
        List<String> imageList = new ArrayList<>();
        // 새로 등록하는 이미지가 없는 경우
        if(multipartFile == null) {
            for (CommentImage commentImage : image) {
                if (!imageRequestDto.getExistUrlList().contains(commentImage.getImageUrl())) {
                    // S3 저장소에서 삭제
                    amazonS3Service.deleteFile(commentImage.getImageUrl().substring(commentImage.getImageUrl().lastIndexOf("/") + 1));
                    // DB 이미지 삭제
                    commentImageRepository.delete(commentImage);
                }
                else {
                    imageList.add(commentImage.getImageUrl());
                }
            }
            return ResponseEntity.ok().body(CommentResponseDto.builder()
                    .comment_id(comment.getId())
                    .place_id(comment.getPlace().getId())
                    .placeTitle(comment.getPlace().getTitle())
                    .nickname(comment.getMember().getNickname())
                    .title(comment.getTitle())
                    .content(comment.getContent())
                    .imageList(imageList)
                    .star(comment.getStar())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build());


            // 새로 등록하는 이미지가 있는 경우
        } else {
            /**
             * 기존에 등록된 이미지 처리
             * 기존 이미지 URL 리스트와 수정해서 넘어오는 URL 리스트를 비교하여
             * 수정해서 넘어오는 URL 리스트에 해당하지 않는 기존 이미지 URL은 사용자가 삭제한 것이므로 삭제 처리
             */
            for (CommentImage commentImage : image) {
                if (!imageRequestDto.getExistUrlList().contains(commentImage.getImageUrl())) {
                    // S3 저장소에서 삭제
                    amazonS3Service.deleteFile(commentImage.getImageUrl().substring(commentImage.getImageUrl().lastIndexOf("/") + 1));
                    // DB 이미지 삭제
                    commentImageRepository.delete(commentImage);
                }
                else {
                    imageList.add(commentImage.getImageUrl());
                }
            }

            // 새로운 이미지 등록
            List<String> imageUrlList;
            imageUrlList = amazonS3Service.upload(multipartFile);
            List<CommentImage> saveImage = new ArrayList<>();

            for (String imageUrls : imageUrlList) {
                CommentImage images = CommentImage.builder()
                        .comment(comment)
                        .member(member)
                        .imageUrl(imageUrls)
                        .build();
                saveImage.add(images);
                imageList.add(images.getImageUrl());
            }
            commentImageRepository.saveAll(saveImage);
            return ResponseEntity.ok().body(CommentResponseDto.builder()
                    .comment_id(comment.getId())
                    .place_id(comment.getPlace().getId())
                    .placeTitle(comment.getPlace().getTitle())
                    .nickname(comment.getMember().getNickname())
                    .title(comment.getTitle())
                    .content(comment.getContent())
                    .imageList(imageList)
                    .star(comment.getStar())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build());

        }

    }

    // 후기 삭제하기
    public ResponseEntity<Long> deleteComment(Long id, UserDetailsImpl userDetails) throws IOException {

        // 로그인한 회원인지 여부 검증
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        // 후기 존재 여부 검증
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));

        // 작성자가 맞는지 여부 검증
        if(!(comment.getMember().getId().equals(member.getId()))) {
            throw new CustomException(DO_NOT_MATCH_USER);
        }


        List<CommentImage> image = commentImageRepository.findAllByCommentId(id);

        // 저장된 이미지가 있으면 S3 저장소에 있는 이미지 삭제하기
        for(int i=0; i<image.size(); i++) {
            amazonS3Service.deleteFile(image.get(i).getImageUrl().substring(image.get(i).getImageUrl().lastIndexOf("/") + 1));
        }
        comment.getPlace().updateStar(comment.getStar()*-1,-1);

        // 후기 삭제
        commentRepository.delete(comment);

        return ResponseEntity.ok().body(id);
    }

    // 내가 쓴 후기 불러오기(마이페이지)
    public ResponseEntity<List<CommentResponseDto>> getMyCommentList(UserDetailsImpl userDetails) {

        // 로그인한 회원인지 여부 검증
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        // 해당 사용자의 모든 후기 불러오기
        List<Comment> commentList = commentRepository.findAllByMemberId(member.getId());
        // 해당 사용자의 후기별 이미지 불러오기

        // 보여줄 데이터
        List<CommentResponseDto> commentResult = new ArrayList<>();

        // 불러온 모든 후기와 후기별 이미지를 comments에 담기
        for (Comment comments : commentList) {
            // 후기별 이미지 리스트 꺼내오기
            List<CommentImage> imageResult = commentImageRepository.findAllByCommentId(comments.getId());
            List<String> imageList = new ArrayList<>();
            // 꺼내온 이미지들을 imageList에 담기
            for(CommentImage images : imageResult) {
                imageList.add(images.getImageUrl());
            }

            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                    .comment_id(comments.getId())
                    .place_id(comments.getPlace().getId())
                    .placeTitle(comments.getPlace().getTitle())
                    .nickname(comments.getMember().getNickname())
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

}
