package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByCommentId(Long comment_id);

    List<Image> findAllByPlaceId(Long place_id);

}
