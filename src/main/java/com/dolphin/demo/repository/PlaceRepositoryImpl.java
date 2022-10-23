package com.dolphin.demo.repository;

import com.dolphin.demo.dto.response.PlaceSearchDto;
import com.dolphin.demo.dto.response.QPlaceSearchDto;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import javax.persistence.EntityManager;
import java.util.List;
import static com.dolphin.demo.domain.QPlace.*;
import static com.dolphin.demo.domain.QPlaceImage.*;

public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PlaceRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<PlaceSearchDto> keywordSearch(String keyword, String pageNum, String areaCode, String sigunguCode) {
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum), 10);
        List<PlaceSearchDto> result = queryFactory
                .select(new QPlaceSearchDto(
                        place.id,
                        place.title,
                        place.star,
                        placeImage.imageUrl,
                        place.readCount,
                        place.count))
                .from(place)
                .leftJoin(place.imageList, placeImage)
                .groupBy(place.id)
                .where(titleContains(keyword), regionSelect(areaCode, sigunguCode))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        int totalSize = queryFactory
                .selectFrom(place)
                .leftJoin(place.imageList, placeImage)
                .groupBy(place.id)
                .where(titleContains(keyword), regionSelect(areaCode, sigunguCode))
                .fetch().size();

        return new PageImpl<>(result, pageRequest, totalSize);
    }

    // 키워드 검색 및 키워드 공백 제거
    private BooleanExpression titleContains(String keyword) {
        // 검색어에서 공백 제거
        String notBlank = keyword.replaceAll(" ", "");
        // 여행지 title의 공백 제거
        StringTemplate tit = Expressions.stringTemplate("REPLACE({0},' ','')", place.title);
        return ObjectUtils.isEmpty(keyword) ? null : tit.containsIgnoreCase(keyword).or(tit.containsIgnoreCase(notBlank));
    }

    // 지역선택 기능
    private BooleanExpression regionSelect(String areaCode, String sigunguCode) {
        if (areaCode.equals("0") & sigunguCode.equals("0")) return null;
        else if(!areaCode.equals("0") & sigunguCode.equals("0")) return place.areaCode.eq(areaCode);
        else return place.areaCode.eq(areaCode).and(place.sigunguCode.eq(sigunguCode));
    }
}
