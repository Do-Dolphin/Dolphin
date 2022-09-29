package com.dolphin.demo.repository;

import com.dolphin.demo.domain.Place;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByAreaCodeAndSigunguCodeAndTheme(String areaCode, String sigunguCode, String theme, PageRequest pageRequest);
    boolean existsByAreaCodeAndSigunguCode(String areaCode, String sigunguCode);
    List<Place> findAllByAreaCodeAndSigunguCodeAndTheme(String areaCode, String sigunguCode, String theme);
    List<Place> findAllByAreaCodeAndTheme(String areaCode, String theme);
    List<Place> findAllByThemeOrderByReadCountDesc(String theme, PageRequest pageRequest);
    Place getTopByOrderByIdDesc();

}
