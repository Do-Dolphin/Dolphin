package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.ImageRequestDto;
import lombok.*;

import javax.persistence.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class CommentImage extends Timestamped {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    @Id
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;






    public void update(Comment comment, CommentImage image) {
        this.comment = comment;
        this.imageUrl = image.getImageUrl();
    }

    public void update(ImageRequestDto imageRequestDto) {
    }


}