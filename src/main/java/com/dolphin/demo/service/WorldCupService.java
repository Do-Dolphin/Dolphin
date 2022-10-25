package com.dolphin.demo.service;

import com.dolphin.demo.domain.Heart;
import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.domain.PlaceImage;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
import com.dolphin.demo.repository.HeartRepository;
import com.dolphin.demo.repository.MemberRepository;
import com.dolphin.demo.repository.PlaceImageRepository;
import com.dolphin.demo.repository.PlaceRepository;
import com.dolphin.demo.dto.request.WorldCupRequestDto;
import com.dolphin.demo.dto.response.WorldCupResponseDto;
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
    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;


    public ResponseEntity<List<WorldCupResponseDto>> makeWorldCup(String areaCode, String sigunguCode, String themes) {
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

        List<WorldCupResponseDto> worldCupResponseDtoList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int index = (int) (Math.random() * placeList.size());
            if(placeImageRepository.findFirstByPlace(placeList.get(index)).isPresent()) {
                worldCupResponseDtoList.add(WorldCupResponseDto.builder()
                        .id(placeList.get(index).getId())
                        .title(placeList.get(index).getTitle())
                        .image(placeImageRepository.findFirstByPlace(placeList.get(index)).get().getImageUrl())
                        .build());
                placeList.remove(index);
            } else{
                worldCupResponseDtoList.add(WorldCupResponseDto.builder()
                        .id(placeList.get(index).getId())
                        .title(placeList.get(index).getTitle())
                        .build());
                placeList.remove(index);
            }
        }
        return new ResponseEntity<>(worldCupResponseDtoList, HttpStatus.OK);
    }

    public ResponseEntity<List<WorldCupResponseDto>> likeWorldCup(String username){
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.UNAUTHORIZED_LOGIN)
        );

        List<Heart> hearts = heartRepository.findAllByMember(member);

        List<WorldCupResponseDto> worldCupResponseDtoList = new ArrayList<>();

        for(int i =0; i<32;i++){
            int index = (int) (Math.random() * hearts.size());
            Place place = hearts.get(index).getPlace();
            PlaceImage img = placeImageRepository.findFirstByPlace(place).orElse(null);
            if (img != null) {
                worldCupResponseDtoList.add(WorldCupResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .image(img.getImageUrl())
                        .build());
                hearts.remove(index);
            }else {
                worldCupResponseDtoList.add(WorldCupResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .build());
                hearts.remove(index);
            }
        }
        return new ResponseEntity<>(worldCupResponseDtoList, HttpStatus.OK);
    }

    public ResponseEntity<List<WorldCupResponseDto>> reWorldCup(WorldCupRequestDto worldCupRequestDto){
        List<WorldCupResponseDto> worldCupResponseDtoList = new ArrayList<>(worldCupRequestDto.getWorldCupResponseDtoList());
        return new ResponseEntity<>(worldCupResponseDtoList, HttpStatus.OK);
    }
}
