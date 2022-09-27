package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.PlaceRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Place {

  @Id
  @Column(name = "place_id")
  private Long id;


  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private float star;

  @Column(nullable = false)
  private int likes;

  @Column(nullable = false)
  private String theme;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String areaCode;

  @Column(nullable = false)
  private String sigunguCode;

  @Column(nullable = false)
  private String mapX;

  @Column(nullable = false)
  private String mapY;

  @Column(nullable = false)
  private int total;

  @Column(nullable = false)
  private int count;


  private Long readCount;

  @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Comment> commentList;

  @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<PlaceImage> imageList;



  public void updateStar(int star, int i){
    this.count += i;
    this.total += star;
    BigDecimal v1 = BigDecimal.valueOf(count);
    BigDecimal v2 = BigDecimal.valueOf(total);
    this.star = v2.divide(v1,1, RoundingMode.HALF_UP).floatValue();
  }

  public void update(PlaceRequestDto requestDto){
    this.title = requestDto.getTitle();
    this.content = requestDto.getContent();
    this.address = requestDto.getAddress();
    this.theme = requestDto.getTheme();
    this.mapX = requestDto.getMapX();
    this.mapY = requestDto.getMapY();
    this.areaCode = requestDto.getAreaCode();
    this.sigunguCode = requestDto.getSigunguCode();
  }

}
