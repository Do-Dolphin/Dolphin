package com.dolphin.demo.controller;

import com.dolphin.demo.dto.request.PlaceRequestDto;
import com.dolphin.demo.dto.request.PlaceUpdateRequestDto;
import com.dolphin.demo.dto.response.*;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<PlaceSortListResponseDto>> getPlace(
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                               @RequestParam("theme") String theme,
                                                               @RequestParam("areaCode") String areaCode,
                                                               @RequestParam("sigunguCode") String sigunguCode,
                                                               @RequestParam("pageNum") String pageNum){
        return placeService.getPlace(theme, areaCode, sigunguCode, pageNum, userDetails);
    }

    @GetMapping("/api/place/random")
    public ResponseEntity<RandomPlaceResponseDto> randomPlace(@RequestParam(value = "areaCode") String areaCode,
                                                              @RequestParam(value = "sigunguCode") String sigunguCode,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails){

        return placeService.randomPlace(areaCode, sigunguCode, userDetails);
    }

    @GetMapping("api/place/rank")
    public ResponseEntity<RankListResponseDto> getRank(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(RankListResponseDto.builder()
                .tourList(placeService.getRank(12, userDetails))
                .museumList(placeService.getRank(14, userDetails))
                .activityList(placeService.getRank(28, userDetails))
                .foodList(placeService.getRank(39, userDetails))
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
        return ResponseEntity.ok(placeService.getPlaceLikeState(id, userDetails));
    }

    @GetMapping("api/auth/place/mypage")
    public ResponseEntity<List<PlaceLikeResponseDto>> getLikePlaceList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestParam(value = "areaCode") String areaCode,
                                                                       @RequestParam(value = "sigunguCode") String sigunguCode) {
        return placeService.getLikePlaceList(areaCode, sigunguCode, userDetails);
    }

}
