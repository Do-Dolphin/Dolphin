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


    @GetMapping("/api/place/search")
    public Page<PlaceSearchDto> keywordSearch(@RequestParam("keyword") String keyword,
                                              @RequestParam("pageNum") String pageNum) {
        return placeRepository.keywordSearch(keyword, pageNum);
    }
}
