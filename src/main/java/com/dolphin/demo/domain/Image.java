package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.ImageRequestDto;
import com.dolphin.demo.dto.response.ImageResponseDto;
import lombok.*;
import javax.persistence.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Image extends Timestamped {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    @Id
    private Long id;

    private String imageUrl;

    private String filename;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "place_id")
    private Place place;


    public Image(ImageResponseDto imageResponseDto) {
        this.imageUrl = imageResponseDto.getImageUrl();
//        this.filename = imageResponseDto.getFilename();
    }


    public void updateImage(ImageRequestDto imageRequestDto) {
        this.imageUrl = imageRequestDto.getImageUrl();
//        this.filename = imageRequestDto.getFilename();
    }

}