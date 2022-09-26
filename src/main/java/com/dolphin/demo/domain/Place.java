package com.dolphin.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

  // 도착 공항
  @Column(nullable = false)
  private String title;
  // 출발 공항
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
  private Long readCount;



  @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Comment> commentList;

  @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<PlaceImage> imageList;

}
