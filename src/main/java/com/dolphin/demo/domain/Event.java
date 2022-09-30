package com.dolphin.demo.domain;

import com.dolphin.demo.dto.request.EventRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String areaCode;
    private String linkUrl;
    private String imageUrl;
    private String period;


    public void event(EventRequestDto eventRequestDto) {
        this.title = eventRequestDto.getTitle();
        this.areaCode = eventRequestDto.getAreaCode();
        this.linkUrl = eventRequestDto.getLinkUrl();
        this.imageUrl = eventRequestDto.getImageUrl();
        this.period = eventRequestDto.getPeriod();
    }

    public void update(EventRequestDto eventRequestDto) {
        this.title = eventRequestDto.getTitle();
        this.areaCode = eventRequestDto.getAreaCode();
        this.linkUrl = eventRequestDto.getLinkUrl();
        this.imageUrl = eventRequestDto.getImageUrl();
        this.period = eventRequestDto.getPeriod();
    }

}
