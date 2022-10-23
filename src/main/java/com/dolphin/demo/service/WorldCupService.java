package com.dolphin.demo.service;

import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.WorldCupRequestDto;
import com.dolphin.demo.dto.response.PlaceListResponseDto;
import com.dolphin.demo.dto.response.WorldCupResponseDto;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
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


    public ResponseEntity<List<List<WorldCupResponseDto>>> makeWorldCup(String areaCode, String sigunguCode, String themes) {
        List<Place> placeList;
        if(areaCode.equals("0")){
            if(themes.equals("0")){
                placeList = placeRepository.findAll();
            }else {
                placeList = placeRepository.findAllByTheme(themes);
            }
            if(placeList.size() < 32){
                throw new CustomException(ErrorCode.FAIL_FIND_AREA);
            }
        }else if(sigunguCode.equals("0")){
            if(themes.equals("0")){
                placeList = placeRepository.findAllByAreaCode(areaCode);
            }else {
                placeList = placeRepository.findAllByAreaCodeAndTheme(areaCode, themes);
            }
            if(placeList.size() < 32){
                throw new CustomException(ErrorCode.FAIL_FIND_AREA);
            }
        }else if(themes.equals("0")){
            placeList = placeRepository.findAllByAreaCodeAndSigunguCode(areaCode, sigunguCode);
            if(placeList.size() < 32){
                throw new CustomException(ErrorCode.FAIL_FIND_AREA);
            }
        }else {
            placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode, sigunguCode, themes);
            if(placeList.size() < 32){
                throw new CustomException(ErrorCode.FAIL_FIND_AREA);
            }
        }

        List<WorldCupResponseDto> placeListResponseDtoList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int index = (int) (Math.random() * placeList.size());
            if(placeImageRepository.findFirstByPlace(placeList.get(index)).isPresent()) {
                System.out.println(placeImageRepository.findFirstByPlace(placeList.get(index)).get().getImageUrl());
                WorldCupResponseDto placeListResponseDto = WorldCupResponseDto.builder()
                        .id(placeList.get(index).getId())
                        .title(placeList.get(index).getTitle())
                        .image(placeImageRepository.findFirstByPlace(placeList.get(index)).get().getImageUrl())
                        .build();
                placeListResponseDtoList.add(placeListResponseDto);
                placeList.remove(index);
            } else{
                WorldCupResponseDto placeListResponseDto = WorldCupResponseDto.builder()
                        .id(placeList.get(index).getId())
                        .title(placeList.get(index).getTitle())
                        .build();
                placeListResponseDtoList.add(placeListResponseDto);
                placeList.remove(index);
            }
        }
        List<List<WorldCupResponseDto>> partition = Lists.partition(placeListResponseDtoList, 2);
        return new ResponseEntity<>(partition, HttpStatus.OK);
    }

    public ResponseEntity<List<List<WorldCupResponseDto>>> reWorldCup(WorldCupRequestDto worldCupRequestDto){
        List<WorldCupResponseDto> worldCupResponseDtoList = new ArrayList<>(worldCupRequestDto.getWorldCupResponseDtoList());
        List<List<WorldCupResponseDto>> partition = Lists.partition(worldCupResponseDtoList, 2);
        return new ResponseEntity<>(partition, HttpStatus.OK);
    }
}
