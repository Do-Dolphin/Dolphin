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
public class PlaceImage extends Timestamped {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    @Id
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;






    public void update(Place place, PlaceImage image) {
        this.place = place;
        this.imageUrl = image.getImageUrl();
    }

    public void update(ImageRequestDto imageRequestDto) {
    }


}