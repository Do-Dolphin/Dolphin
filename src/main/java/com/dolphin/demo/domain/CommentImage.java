package com.dolphin.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class CommentImage extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_image_id")
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

}
