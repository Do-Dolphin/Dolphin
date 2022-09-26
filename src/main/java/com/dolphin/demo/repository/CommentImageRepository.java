package com.dolphin.demo.repository;

import com.dolphin.demo.domain.CommentImage;
import com.dolphin.demo.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentImageRepository extends JpaRepository<CommentImage, Long> {

    List<CommentImage> findAllByCommentId(Long comment_id);
}
