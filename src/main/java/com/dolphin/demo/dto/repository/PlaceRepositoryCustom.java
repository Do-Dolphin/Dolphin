package com.dolphin.demo.dto.repository;

import com.dolphin.demo.dto.response.PlaceSearchDto;
import org.springframework.data.domain.Page;

public interface PlaceRepositoryCustom {
    Page<PlaceSearchDto> keywordSearch(String keyword, String pageNum, String areaCode, String sigunguCode);
}
