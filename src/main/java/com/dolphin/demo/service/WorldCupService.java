package com.dolphin.demo.service;

import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.response.PlaceListResponseDto;
import com.dolphin.demo.repository.PlaceImageRepository;
import com.dolphin.demo.repository.PlaceRepository;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WorldCupService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;


    public ResponseEntity<List<List<PlaceListResponseDto>>> makeWorldCup() {
        List<Place> placeList = placeRepository.findAll();
        List<PlaceListResponseDto> placeListResponseDtoList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int index = (int) (Math.random() * placeList.size());
            if(placeImageRepository.findFirstByPlace(placeList.get(index)).isPresent()) {
                System.out.println(placeImageRepository.findFirstByPlace(placeList.get(index)).get().getImageUrl());
                PlaceListResponseDto placeListResponseDto = PlaceListResponseDto.builder()
                        .id(placeList.get(index).getId())
                        .title(placeList.get(index).getTitle())
                        .image(placeImageRepository.findFirstByPlace(placeList.get(index)).get().getImageUrl())
                        .build();
                placeListResponseDtoList.add(placeListResponseDto);
            } else{
                PlaceListResponseDto placeListResponseDto = PlaceListResponseDto.builder()
                        .id(placeList.get(index).getId())
                        .title(placeList.get(index).getTitle())
                        .build();
                placeListResponseDtoList.add(placeListResponseDto);
            }
        }
        List<List<PlaceListResponseDto>> partition = Lists.partition(placeListResponseDtoList, 2);
        return new ResponseEntity<>(partition, HttpStatus.OK);
    }
}
