package com.dolphin.demo.controller;

import com.dolphin.demo.dto.request.WorldCupRequestDto;
import com.dolphin.demo.dto.response.PlaceListResponseDto;
import com.dolphin.demo.dto.response.WorldCupResponseDto;
import com.dolphin.demo.service.WorldCupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class WorldCupController {

    private final WorldCupService worldCupService;

    @GetMapping(value = "/api/place/worldcup")
    public ResponseEntity<List<List<WorldCupResponseDto>>> makeWorldCup(){
        return worldCupService.makeWorldCup();
    }

    @PostMapping(value = "/api/place/reworldcup")
    public ResponseEntity<List<List<WorldCupResponseDto>>> reWorldCup(@RequestBody WorldCupRequestDto worldCupRequestDto){
        return worldCupService.reWorldCup(worldCupRequestDto);
    }
}
