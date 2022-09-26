package com.dolphin.demo.repository;

import com.dolphin.demo.domain.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {

    List<PlaceImage> findAllByPlaceId(Long place_id);
    PlaceImage findByPlaceId(Long place_id);

}
