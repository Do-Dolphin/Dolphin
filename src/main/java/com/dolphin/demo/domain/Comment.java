package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.CommentRequestDto;
import com.dolphin.demo.dto.request.PlaceRequestDto;
import com.dolphin.demo.dto.response.ImageResponseDto;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment extends Timestamped {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank
    @Column(nullable = false)
    private String title;


    @NotBlank
    @Column(nullable = false)
    private String content;


    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Image> imageList;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Column
    private int star;

//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;


    public Comment(CommentRequestDto commentRequestDto, PlaceRequestDto placeRequestDto, List<Image> imageList) {
        this.title = commentRequestDto.getTitle();
        this.content = commentRequestDto.getContent();
        this.star = placeRequestDto.getStar();
        this.imageList = imageList;
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.title = commentRequestDto.getTitle();
        this.content = commentRequestDto.getContent();
    }
}
