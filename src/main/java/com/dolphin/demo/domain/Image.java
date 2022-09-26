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






    public void update(Image image) {
        this.imageUrl = image.getImageUrl();
    }

    public void update(ImageRequestDto imageRequestDto) {
    }


}