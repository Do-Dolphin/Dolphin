package com.dolphin.demo.controller;

import com.dolphin.demo.dto.request.WorldCupRequestDto;
import com.dolphin.demo.dto.response.WorldCupResponseDto;
import com.dolphin.demo.service.WorldCupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class WorldCupController {

    private final WorldCupService worldCupService;

    @GetMapping(value = "/api/place/worldcup")
    public ResponseEntity<List<List<WorldCupResponseDto>>> makeWorldCup(@RequestParam(value = "areaCode") String areaCode,
                                                                        @RequestParam(value = "sigunguCode") String sigunguCode,
                                                                        @RequestParam(value = "themes") String themes){
        return worldCupService.makeWorldCup(areaCode, sigunguCode, themes);
    }

    @GetMapping(value = "/api/auth/place/worldcup")
    public ResponseEntity<List<List<WorldCupResponseDto>>> likeWorldCup(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return worldCupService.likeWorldCup(userDetails.getUsername());
    }

    @PostMapping(value = "/api/place/reworldcup")
    public ResponseEntity<List<List<WorldCupResponseDto>>> reWorldCup(@RequestBody WorldCupRequestDto worldCupRequestDto){
        return worldCupService.reWorldCup(worldCupRequestDto);
    }
}
