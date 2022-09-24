package com.dolphin.demo.controller;

import com.dolphin.demo.dto.response.PlaceListResponseDto;
import com.dolphin.demo.dto.response.PlaceResponseDto;
import com.dolphin.demo.dto.response.RandomPlaceResponseDto;
import com.dolphin.demo.dto.response.RankListResponseDto;
import com.dolphin.demo.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/api/place")
    public ResponseEntity<List<PlaceListResponseDto>> getPlace(@RequestParam("theme") String theme, @RequestParam("areaCode") String areaCode, @RequestParam("sigunguCode") String sigunguCode, @RequestParam("pageNum") String pageNum){
        return placeService.getPlace(theme, areaCode, sigunguCode, pageNum);
    }

    @GetMapping("/api/place/random")
    public ResponseEntity<RandomPlaceResponseDto> randomPlace(){
        return placeService.randomPlace();
    }

    @GetMapping("api/place/rank")
    public ResponseEntity<RankListResponseDto> getRank(){
        return ResponseEntity.ok(RankListResponseDto.builder()
                .tourList(placeService.getRank(12))
                .museumList(placeService.getRank(14))
                .activityList(placeService.getRank(28))
                .foodList(placeService.getRank(39))
                .build());
    }

}
