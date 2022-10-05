package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.PlaceUpdateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

  //장소 이름
  @Column(nullable = false)
  private String title;

  //장소 설명
  @Column(nullable = false)
  private String content;

  //장소에 대한 후기 평점
  @Column(nullable = false)
  private float star;

  //찜한 멤버들의 수
  @Column(nullable = false)
  private int likes;

  //테마 코드
  @Column(nullable = false)
  private String theme;

  //장소 주소
  @Column(nullable = false)
  private String address;

  //지역 코드
  @Column(nullable = false)
  private String areaCode;

  //시군구 코드
  @Column(nullable = false)
  private String sigunguCode;

  //X좌표
  @Column(nullable = false)
  private String mapX;

  //Y좌표
  @Column(nullable = false)
  private String mapY;

  //평점 총 합
  @Column(nullable = false)
  private int total;

  //후기 개수
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
    if(count == 0) {
      this.star = 0;
    }
    else {
      BigDecimal v1 = BigDecimal.valueOf(count);
      BigDecimal v2 = BigDecimal.valueOf(total);
      this.star = v2.divide(v1, 1, RoundingMode.HALF_UP).floatValue();
    }
  }

  public void update(PlaceUpdateRequestDto requestDto, String x, String y){
    this.title = requestDto.getTitle();
    this.content = requestDto.getContent();
    this.address = requestDto.getAddress();
    this.theme = requestDto.getTheme();
    this.mapX = x;
    this.mapY = y;
    this.areaCode = requestDto.getAreaCode();
    this.sigunguCode = requestDto.getSigunguCode();
  }

  public void udateLikes(int likes){
    this.likes = likes;
  }

  public void updateContent(String content){
    this.content = content;
  }
}
