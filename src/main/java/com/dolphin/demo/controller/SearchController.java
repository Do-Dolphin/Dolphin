package com.dolphin.demo.controller;

import com.dolphin.demo.dto.response.PlaceSearchDto;
import com.dolphin.demo.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class SearchController {

    private final PlaceRepository placeRepository;


    // 키워드로 여행지 검색
    @GetMapping("/api/place/search")
    public Page<PlaceSearchDto> keywordSearch(@RequestParam("keyword") String keyword,
                                              @RequestParam("pageNum") String pageNum,
                                              @RequestParam("areaCode") String areaCode,
                                              @RequestParam("sigunguCode") String sigunguCode) {
        return placeRepository.keywordSearch(keyword, pageNum, areaCode, sigunguCode);
    }
}
