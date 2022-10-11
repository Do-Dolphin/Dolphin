package com.dolphin.demo.repository;

import com.dolphin.demo.dto.response.PlaceSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaceRepositoryCustom {
    Page<PlaceSearchDto> keywordSearch(String keyword, String pageNum);
}
