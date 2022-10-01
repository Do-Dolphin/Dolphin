package com.dolphin.demo.controller;

import com.dolphin.demo.dto.request.PlaceRequestDto;
import com.dolphin.demo.dto.request.PlaceUpdateRequestDto;
import com.dolphin.demo.dto.response.*;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/api/place")
    public ResponseEntity<List<PlaceListResponseDto>> getPlace(@RequestParam("theme") String theme,
                                                               @RequestParam("areaCode") String areaCode,
                                                               @RequestParam("sigunguCode") String sigunguCode,
                                                               @RequestParam("pageNum") String pageNum){
        return placeService.getPlace(theme, areaCode, sigunguCode, pageNum);
    }

    @GetMapping("/api/place/random")
    public ResponseEntity<RandomPlaceResponseDto> randomPlace(){

        return placeService.randomPlace();
    }

    @GetMapping("api/place/rank")
    public ResponseEntity<RankListResponseDto> getRank(){
        return ResponseEntity.ok(RankListResponseDto.builder()
                .tourList(placeService.getRank(12))
                .museumList(placeService.getRank(14))
                .activityList(placeService.getRank(28))
                .foodList(placeService.getRank(39))
                .build());
    }

    @GetMapping("api/place/{id}")
    public ResponseEntity<PlaceResponseDto> getPlaceDetail(@PathVariable Long id) {
        return placeService.getPlaceDetail(id);
    }

//    @Secured("ADMIN")
    @PostMapping("api/auth/place")
    public ResponseEntity<PlaceResponseDto> createPlace(@RequestPart("data")PlaceRequestDto placeRequestDto,
                                                        @RequestPart(value = "image", required = false)List<MultipartFile> multipartFile) throws IOException {
        return placeService.createPlace(placeRequestDto, multipartFile);
    }

    //    @Secured("ADMIN")
    @PutMapping("api/auth/place/{id}")
    public ResponseEntity<PlaceResponseDto> updatePlace(@PathVariable Long id,
                                                        @RequestPart("data") PlaceUpdateRequestDto placeRequestDto,
                                                        @RequestPart(value = "image", required = false)List<MultipartFile> multipartFile) throws IOException {
        return placeService.updatePlace(id, placeRequestDto, multipartFile);
    }

    //    @Secured("ADMIN")
    @DeleteMapping("api/auth/place/{id}")
    public ResponseEntity<String> deletePlace(@PathVariable Long id) {
        return placeService.deletePlace(id);
    }

    @PostMapping("api/auth/place/like/{id}")
    public ResponseEntity<HeartResponseDto> likePlace(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable Long id){
        return placeService.likePlace(id, userDetails);
    }

    @GetMapping("api/place/like/{id}")
    public ResponseEntity<Boolean> getPlaceLikeState(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long id){
        return placeService.getPlaceLikeState(id, userDetails);
    }

    @GetMapping("api/auth/place/like")
    public ResponseEntity<List<PlaceListResponseDto>> getLikePlaceList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return placeService.getLikePlaceList(userDetails);
    }

}
