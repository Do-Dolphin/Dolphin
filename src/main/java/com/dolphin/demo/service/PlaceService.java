package com.dolphin.demo.service;

import com.dolphin.demo.domain.Heart;
import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.PlaceImage;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.PlaceRequestDto;
import com.dolphin.demo.dto.request.PlaceUpdateRequestDto;
import com.dolphin.demo.dto.response.*;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.repository.HeartRepository;
import com.dolphin.demo.repository.MemberRepository;
import com.dolphin.demo.repository.PlaceImageRepository;
import com.dolphin.demo.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository imageRepository;
    private final AmazonS3Service amazonS3Service;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;

    @Value("${kakaoAPI.key}")
    String key;

    //지역, 테마에 해당하는 place 리스트 반환(페이지)
    public ResponseEntity<List<PlaceSortListResponseDto>> getPlace(String theme, String areaCode, String sigunguCode, String pageNum, UserDetailsImpl userDetails) {
        List<PlaceSortListResponseDto> responseDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum), 10);
        List<Place> placeList;
        if (sigunguCode.equals("0")) {
            if (areaCode.equals("0"))
                placeList = placeRepository.findAllByTheme(theme, pageRequest);
            else
                placeList = placeRepository.findAllByAreaCodeAndTheme(areaCode, theme, pageRequest);
        } else {
            placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode, sigunguCode, theme, pageRequest);
        }

        for (Place place : placeList) {
            PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
            if (img != null)
                responseDtoList.add(PlaceSortListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .image(img.getImageUrl())
                        .state(getPlaceLikeState(place.getId(), userDetails))
                        .commentCount(place.getCount())
                        .readCount(place.getReadCount())
                        .build());
            else
                responseDtoList.add(PlaceSortListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .commentCount(place.getCount())
                        .readCount(place.getReadCount())
                        .state(getPlaceLikeState(place.getId(), userDetails))
                        .build());
        }

        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * 랜덤으로 지역을 하나 추천해준다.
     * 지역을 입력 받으면 지역내에서 테마별 장소를 랜덤으로 하나씩 뽑아서 보내준다.
     * 지역 코드에 0이 들어오면 지역 무관 전체 랜덤
     */
    public ResponseEntity<RandomPlaceResponseDto> randomPlace(String areaCode, String sigunguCode, UserDetailsImpl userDetails) {
        List<PlaceListResponseDto> randomList = new ArrayList<>();
        int areaNum;
        int sigunguNum;
        if (areaCode.equals("0")) {
            areaNum = (int) (Math.random() * 17 + 1);
            if (areaNum > 8) {
                areaNum += 22;
            }
        } else {
            areaNum = Integer.parseInt(areaCode);
        }

        String[] themes = {"12", "14", "28", "39"};
        String area;


        if (areaNum > 8) {
            if (sigunguCode.equals("0"))
                while (true) {
                    sigunguNum = (int) (Math.random() * 31 + 1);
                    if (placeRepository.existsByAreaCodeAndSigunguCode(String.valueOf(areaNum), String.valueOf(sigunguNum)))
                        break;
                }
            else
                sigunguNum = Integer.parseInt(sigunguCode);
            for (String themeCode : themes) {
                randomList.add(randomSigungu(String.valueOf(areaNum), String.valueOf(sigunguNum), themeCode, userDetails));
            }
            area = getArea(randomList.get(0), 1);
        } else {
            for (String themeCode : themes) {
                randomList.add(randomArea(String.valueOf(areaNum), themeCode, userDetails));
            }
            area = getArea(randomList.get(0), 0);

        }


        return ResponseEntity.ok(RandomPlaceResponseDto.builder()
                .placeList(randomList)
                .area(area)
                .build());
    }


    // 광역시, 특별시에 해당하는 지역에서 해당하는 테마의 관광지 랜덤 추첨
    public PlaceListResponseDto randomArea(String areaCode, String theme, UserDetailsImpl userDetails) {

        List<Place> placeList = placeRepository.findAllByAreaCodeAndTheme(String.valueOf(areaCode), theme);

        int index = (int) (Math.random() * placeList.size());
        Place place = placeList.get(index);
        PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
        if (img != null)
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .image(img.getImageUrl())
                    .theme(place.getTheme())
                    .state(getPlaceLikeState(place.getId(), userDetails))
                    .build();
        else
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .theme(place.getTheme())
                    .state(getPlaceLikeState(place.getId(), userDetails))
                    .build();

    }

    /**
     * 랜덤으로 돌린 지역 이름을 주소에서 추출
     * n은 0 또는 1이다.
     * 0은 광역시, 특별시 등에 해당하는 지역
     * 1은 도 내에 있는 시, 군 지역
     */
    public String getArea(PlaceListResponseDto responseDto, int n) {
        Place place = placeRepository.findById(responseDto.getId()).orElse(null);
        StringBuilder s = new StringBuilder();
        s.append(place.getAddress().split("특별시|특별자치도|특별자치시|광역시| ")[0]);
        for (int i = 1; i <= n; i++) {
            s.append(" ");
            s.append(place.getAddress().split(" ")[n]);
        }
        return s.toString();
    }

    //도 내에 시, 군 지역들의 해당하는 테마 랜덤 여행지 추첨
    public PlaceListResponseDto randomSigungu(String areaCode, String sigunguCode, String theme, UserDetailsImpl userDetails) {

        List<Place> placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode, sigunguCode, theme);
        System.out.println(areaCode + " " + sigunguCode);
        int index = (int) (Math.random() * placeList.size());
        Place place = placeList.get(index);

        PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
        if (img != null)
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .image(img.getImageUrl())
                    .theme(place.getTheme())
                    .state(getPlaceLikeState(place.getId(), userDetails))
                    .build();
        else
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .theme(place.getTheme())
                    .state(getPlaceLikeState(place.getId(), userDetails))
                    .build();

    }


    //한국관광공사 api 에서 제공하는 조회수 기준으로 테마별 top10을 보여주는 메서드
    public List<PlaceListResponseDto> getRank(int theme, UserDetailsImpl userDetails) {
        PageRequest pageRequest = PageRequest.of(1, 10);
        List<PlaceListResponseDto> responseDtoList = new ArrayList<>();
        List<Place> placeList = placeRepository.findAllByThemeOrderByReadCountDesc(String.valueOf(theme), pageRequest);
        for (Place place : placeList) {
            PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
            if (img != null)
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .image(img.getImageUrl())
                        .theme(place.getTheme())
                        .state(getPlaceLikeState(place.getId(), userDetails))
                        .build());
            else
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .theme(place.getTheme())
                        .state(getPlaceLikeState(place.getId(), userDetails))
                        .build());
        }
        return responseDtoList;

    }

    //여행지 상세 내용을 반환
    public ResponseEntity<PlaceResponseDto> getPlaceDetail(Long id) {

        Place place = placeRepository.findById(id).orElse(null);

        if (place == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PLACE);

        List<PlaceImage> img = imageRepository.findAllByPlace(place);
        List<String> images = new ArrayList<>();
        if (!img.isEmpty())
            for (PlaceImage image : img) {
                images.add(image.getImageUrl());
            }

        return ResponseEntity.ok(PlaceResponseDto.builder()
                .id(place.getId())
                .title(place.getTitle())
                .content(place.getContent())
                .address(place.getAddress())
                .theme(place.getTheme())
                .likes(place.getLikes())
                .star(place.getStar())
                .imageUrl(images)
                .mapX(place.getMapX())
                .mapY(place.getMapY())
                .build());

    }

    //api service에서 조회한 content를 place에 업데이트 하는 메서드
    @Transactional
    public void updateContent(Long id, String content) {
        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PLACE);
        place.updateContent(content);
    }


    //api service에서 추가 이미지를 가져오기 위해 탐색한 이미지의 상태를 처리하는 메서드
    @Transactional
    public void updateState(Long id, boolean state) {
        PlaceImage placeImage = imageRepository.findById(id).orElse(null);
        placeImage.updateState(state);
    }

    //장소 생성
    @Transactional
    public ResponseEntity<PlaceResponseDto> createPlace(PlaceRequestDto requestDto, List<MultipartFile> multipartFile) throws IOException {
        Long id = placeRepository.findTopByOrderByIdDesc().getId();
        if (id < 5000000)
            id = 4999999L;
        String[] coordinates = getCoordinates(requestDto.getAddress());
        Place place = Place.builder()
                .id(id + 1)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .address(requestDto.getAddress())
                .areaCode(requestDto.getAreaCode())
                .sigunguCode(requestDto.getSigunguCode())
                .star(0)
                .theme(requestDto.getTheme())
                .likes(0)
                .mapX(coordinates[0])
                .mapY(coordinates[1])
                .readCount(0L)
                .build();

        placeRepository.save(place);


        // 이미지 등록하기
        List<String> imageUrlList;
        List<String> imageList = new ArrayList<>();

        imageUrlList = amazonS3Service.upload(multipartFile);
        List<PlaceImage> saveImages = new ArrayList<>();
        for (String imageUrl : imageUrlList) {
            PlaceImage image = PlaceImage.builder()
                    .place(place)
                    .imageUrl(imageUrl)
                    .build();
            saveImages.add(image);
            imageList.add(image.getImageUrl());
        }

        imageRepository.saveAll(saveImages);
        return ResponseEntity.ok().body(PlaceResponseDto.builder()
                .id(place.getId())
                .title(place.getTitle())
                .content(place.getContent())
                .address(place.getAddress())
                .star(place.getStar())
                .theme(place.getTheme())
                .likes(place.getLikes())
                .mapX(place.getMapX())
                .mapY(place.getMapY())
                .imageUrl(imageList)
                .build());

    }

    // 장소 수정
    @Transactional
    public ResponseEntity<PlaceResponseDto> updatePlace(Long id, PlaceUpdateRequestDto placeRequestDto, List<MultipartFile> multipartFiles) throws IOException {
        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PLACE);
        String[] coordinates = getCoordinates(placeRequestDto.getAddress());
        place.update(placeRequestDto, coordinates[0], coordinates[1]);

        List<PlaceImage> images = imageRepository.findAllByPlace(place);
        List<String> imageUrlList;
        List<String> requestImages = new ArrayList<>();


        // 버킷에서 이미지 삭제
        for (PlaceImage placeImage : images) {
            if (!placeRequestDto.getExistUrlList().contains(placeImage.getImageUrl())) {
                amazonS3Service.deleteFile(placeImage.getImageUrl().substring(placeImage.getImageUrl().lastIndexOf("/") + 1));
                imageRepository.delete(placeImage);
            } else {
                requestImages.add(placeImage.getImageUrl());
            }
        }

        if (multipartFiles != null) {
            images.clear();
            imageUrlList = amazonS3Service.upload(multipartFiles);
            for (String imageUrl : imageUrlList) {
                PlaceImage image = PlaceImage.builder()
                        .place(place)
                        .imageUrl(imageUrl)
                        .build();
                images.add(image);
                requestImages.add(image.getImageUrl());
            }
            imageRepository.saveAll(images);
        }

        return ResponseEntity.ok().body(PlaceResponseDto.builder()
                .id(place.getId())
                .title(place.getTitle())
                .content(place.getContent())
                .address(place.getAddress())
                .star(place.getStar())
                .theme(place.getTheme())
                .likes(place.getLikes())
                .mapX(place.getMapX())
                .mapY(place.getMapY())
                .imageUrl(requestImages)
                .build());
    }

    //장소 삭제
    @Transactional
    public ResponseEntity<String> deletePlace(Long id) {

        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PLACE);
        List<PlaceImage> images = imageRepository.findAllByPlace(place);
        // 버킷에서 이미지 삭제
        for (int i = 0; i < images.size(); i++) {
            amazonS3Service.deleteFile(images.get(i).getImageUrl().substring(images.get(i).getImageUrl().lastIndexOf("/") + 1));
        }
        heartRepository.deleteAllByPlace(place);
        placeRepository.delete(place);


        return ResponseEntity.ok("delete place: " + id);
    }

    //장소 찜하기
    @Transactional
    public ResponseEntity<HeartResponseDto> likePlace(Long id, UserDetailsImpl userDetails) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PLACE);

        boolean state;

        Heart heart = heartRepository.findByMemberAndPlace(member, place).orElse(null);
        if (heart == null) {
            heartRepository.save(Heart.builder()
                    .place(place)
                    .member(member)
                    .build());
            state = true;
        } else {
            heartRepository.delete(heart);
            state = false;
        }
        place.udateLikes(heartRepository.countByPlace(place));
        return ResponseEntity.ok(HeartResponseDto.builder()
                .state(state)
                .count(place.getLikes())
                .build());

    }

    /**
     * 입력받은 id의 장소에서 사용자의 찜 상태
     * 사용자가 로그인을 하지 않았거나, 찜 하지 않았다면 false
     * 사용자가 찜을 한 상태라면 true
     */
    public boolean getPlaceLikeState(Long id, UserDetailsImpl userDetails) {
        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PLACE);

        boolean state = false;
        if (userDetails != null) {
            Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (member != null) {
                Heart heart = heartRepository.findByMemberAndPlace(member, place).orElse(null);
                if (heart != null) {
                    state = true;
                }
            }
        }
        return state;
    }

    //사용자가 찜한 장소들의 리스트 반환
    public ResponseEntity<List<PlaceLikeResponseDto>> getLikePlaceList(UserDetailsImpl userDetails) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        List<Heart> hearts = heartRepository.findAllByMember(member);

        List<PlaceLikeResponseDto> responseDtoList = new ArrayList<>();

        for (Heart heart : hearts) {
            Place place = heart.getPlace();
            PlaceImage img = imageRepository.findFirstByPlace(place).orElse(null);
            if (img != null)
                responseDtoList.add(PlaceLikeResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .image(img.getImageUrl())
                        .theme(place.getTheme())
                        .state(getPlaceLikeState(place.getId(), userDetails))
                        .mapX(place.getMapX())
                        .mapY(place.getMapY())
                        .build());
            else
                responseDtoList.add(PlaceLikeResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .theme(place.getTheme())
                        .state(getPlaceLikeState(place.getId(), userDetails))
                        .mapX(place.getMapX())
                        .mapY(place.getMapY())
                        .build());
        }
        return ResponseEntity.ok(responseDtoList);

    }


    //입력받은 주소를 카카오 api를 통해 좌표값을 얻어내는 메서드
    public String[] getCoordinates(String address) {
        String[] coordinates = new String[2];
        try {
            StringBuilder urlStr = new StringBuilder("https://dapi.kakao.com/v2/local/search/address.json");
            urlStr.append("?size=1");
            urlStr.append("&page=1");
            urlStr.append("&analyze_type=exac");
            urlStr.append("&query=").append(URLEncoder.encode(address, "UTF-8"));


            URL url = new URL(urlStr.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "KakaoAK "+ key);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(result);
            JSONArray addresses = (JSONArray) jsonObject.get("documents");
            JSONObject adress = (JSONObject)addresses.get(0);

            coordinates[0] = adress.get("x").toString();
            coordinates[1] = adress.get("y").toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return coordinates;
    }

}