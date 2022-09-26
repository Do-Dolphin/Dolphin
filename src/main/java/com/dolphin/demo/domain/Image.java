package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.ImageRequestDto;
import lombok.*;
import javax.persistence.*;
import java.util.List;


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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;





    public void update(Comment comment, Place place, Image image) {
        this.comment = comment;
        this.place = place;
        this.imageUrl = image.getImageUrl();
        this.filename = image.getFilename();
    }

    public void update(ImageRequestDto imageRequestDto) {
    }


}