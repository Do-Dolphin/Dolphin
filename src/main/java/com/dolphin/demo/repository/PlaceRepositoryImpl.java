package com.dolphin.demo.repository;

import com.dolphin.demo.dto.response.PlaceSearchDto;
import com.dolphin.demo.dto.response.QPlaceSearchDto;
import com.querydsl.core.types.dsl.BooleanExpression;
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
        System.out.println(areaCode);

        int totalSize = queryFactory
                .selectFrom(place)
                .leftJoin(place.imageList, placeImage)
                .groupBy(place.id)
                .where(titleContains(keyword), regionSelect(areaCode, sigunguCode))
                .fetch().size();

        return new PageImpl<>(result, pageRequest, totalSize);
    }

    private BooleanExpression titleContains(String keyword) {
        String notBlank = keyword.replaceAll(" ", "");
        return ObjectUtils.isEmpty(keyword) ? null : place.title.containsIgnoreCase(keyword).or(place.title.containsIgnoreCase(notBlank));
    }

    private BooleanExpression regionSelect(String areaCode, String sigunguCode) {
        return areaCode.equals("0") & sigunguCode.equals("0") ? null : place.areaCode.eq(areaCode).and(place.sigunguCode.eq(sigunguCode));
    }
}
