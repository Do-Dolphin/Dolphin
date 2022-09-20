package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.ImageRequestDto;
import lombok.*;
import javax.persistence.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Image {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    @Id
    private Long id;

    private String imageUrl;

    private String filename;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    public Image(Image image) {
        this.imageUrl = image.getImageUrl();
        this.filename = image.getFilename();
    }


    public void updateImage(ImageRequestDto imageRequestDto) {
        this.imageUrl = imageRequestDto.getImageUrl();
        this.filename = imageRequestDto.getFilename();
    }

}