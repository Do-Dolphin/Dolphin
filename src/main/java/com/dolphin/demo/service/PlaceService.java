package com.dolphin.demo.service;

import com.dolphin.demo.domain.Heart;
import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.PlaceImage;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.PlaceRequestDto;
import com.dolphin.demo.dto.request.PlaceUpdateRequestDto;
import com.dolphin.demo.dto.response.HeartResponseDto;
import com.dolphin.demo.dto.response.PlaceListResponseDto;
import com.dolphin.demo.dto.response.PlaceResponseDto;
import com.dolphin.demo.dto.response.RandomPlaceResponseDto;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.repository.HeartRepository;
import com.dolphin.demo.repository.MemberRepository;
import com.dolphin.demo.repository.PlaceImageRepository;
import com.dolphin.demo.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
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
    @Value("${restAPI.key}")
    String apiKey;

    //지역, 테마에 해당하는 place 리스트 반환(페이지)
    public ResponseEntity<List<PlaceListResponseDto>> getPlace(String theme, String areaCode, String sigunguCode, String pageNum) {
        List<PlaceListResponseDto> responseDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum), 10);

        List<Place> placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode, sigunguCode, theme, pageRequest);
        for (Place place : placeList) {
            PlaceImage img = imageRepository.findByPlaceId(place.getId()).orElse(null);
            if (img != null)
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .image(img.getImageUrl())
                        .build());
            else
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .build());
        }

        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * 랜덤으로 지역을 하나 추천해준다.
     * 이 지역 내에서 테마별 장소를 랜덤으로 하나씩 뽑아서 보내준다.
     */
    public ResponseEntity<RandomPlaceResponseDto> randomPlace() {
        List<PlaceListResponseDto> randomList = new ArrayList<>();
        int areaCode = (int) (Math.random() * 17 + 1);
        int sigunguCode;
        String[] themes = {"12", "14", "28", "39"};
        String area;
        if (areaCode > 8) {
            areaCode += 22;

            while (true) {
                sigunguCode = (int) (Math.random() * 31 + 1);

                if (placeRepository.existsByAreaCodeAndSigunguCode(String.valueOf(areaCode), String.valueOf(sigunguCode)))
                    break;
            }

            for (String theme : themes) {
                randomList.add(randomSigungu(String.valueOf(areaCode), String.valueOf(sigunguCode), theme));
            }
            area = getArea(randomList.get(0), 1);

        } else {
            for (String theme : themes) {

                randomList.add(randomArea(String.valueOf(areaCode), theme));
            }
            area = getArea(randomList.get(0), 0);
        }


        return ResponseEntity.ok(RandomPlaceResponseDto.builder()
                .placeList(randomList)
                .area(area)
                .build());
    }

    //태그에 해당하는 값 추출
    public String getTagValue(String tag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag);
        if (null != nlList.item(0) && nlList.item(0).getChildNodes().item(0) != null)
            result = nlList.item(0).getChildNodes().item(0).getTextContent();

        return result;
    }

    // 광역시, 특별시에 해당하는 지역에서 해당하는 테마의 관광지 랜덤 추첨
    public PlaceListResponseDto randomArea(String areaCode, String theme) {

        List<Place> placeList = placeRepository.findAllByAreaCodeAndTheme(String.valueOf(areaCode), theme);

        int index = (int) (Math.random() * placeList.size());
        Place place = placeList.get(index);
        PlaceImage img = imageRepository.findByPlaceId(place.getId()).orElse(null);
        if (img != null)
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .image(img.getImageUrl())
                    .theme(place.getTheme())
                    .build();
        else
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .theme(place.getTheme())
                    .build();

    }

    /** 랜덤으로 돌린 지역 이름을 주소에서 추출
     * n은 0 또는 1이다.
     *  0은 광역시, 특별시 등에 해당하는 지역
     *  1은 도 내에 있는 시, 군 지역
     */
    public String getArea(PlaceListResponseDto responseDto, int n) {
        Place place = placeRepository.findById(responseDto.getId()).orElse(null);
        StringBuilder s = new StringBuilder();
        s.append(place.getAddress().split(" ")[0]);
        for (int i = 1; i <= n; i++) {
            s.append(" ");
            s.append(place.getAddress().split(" ")[n]);
        }
        return s.toString();
    }

    //도 내에 시, 군 지역들의 해당하는 테마 랜덤 여행지 추첨
    public PlaceListResponseDto randomSigungu(String areaCode, String sigungu, String theme) {

        List<Place> placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode, sigungu, theme);

        int index = (int) (Math.random() * placeList.size());
        Place place = placeList.get(index);

        PlaceImage img = imageRepository.findByPlaceId(place.getId()).orElse(null);
        if (img != null)
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .image(img.getImageUrl())
                    .theme(place.getTheme())
                    .build();
        else
            return PlaceListResponseDto.builder()
                    .id(place.getId())
                    .title(place.getTitle())
                    .star(place.getStar())
                    .theme(place.getTheme())
                    .build();

    }


    public List<PlaceListResponseDto> getRank(int theme) {
        PageRequest pageRequest = PageRequest.of(1, 10);
        List<PlaceListResponseDto> responseDtoList = new ArrayList<>();
        List<Place> placeList = placeRepository.findAllByThemeOrderByReadCountDesc(String.valueOf(theme), pageRequest);
        for (Place place : placeList) {
            PlaceImage img = imageRepository.findByPlaceId(place.getId()).orElse(null);
            if (img != null)
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .image(img.getImageUrl())
                        .build());
            else
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .build());
        }
        return responseDtoList;

    }

    //여행지 상세 내용을 반환
    public ResponseEntity<PlaceResponseDto> getPlaceDetail(Long id) {

        Place place = placeRepository.findById(id).orElse(null);

        if (place == null)
            throw new CustomException(ErrorCode.Not_Found_Place);

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

    //장소 생성
    @Transactional
    public ResponseEntity<PlaceResponseDto> createPlace(UserDetailsImpl userDetails, PlaceRequestDto requestDto, List<MultipartFile> multipartFile) throws IOException {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Long id = placeRepository.getTopByOrderByIdDesc().getId();
        if (id < 5000000)
            id = 4999999L;
        Place place = Place.builder()
                .id(id + 1)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .address(requestDto.getAddress())
                .areaCode(requestDto.getAddress())
                .sigunguCode(requestDto.getAddress())
                .star(0)
                .theme(requestDto.getTheme())
                .likes(0)
                .mapX(requestDto.getMapX())
                .mapY(requestDto.getMapY())
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
    public ResponseEntity<PlaceResponseDto> updatePlace(UserDetailsImpl userDetails, Long id, PlaceUpdateRequestDto placeRequestDto, List<MultipartFile> multipartFiles) throws IOException {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.Not_Found_Place);

        place.update(placeRequestDto);

        List<PlaceImage> images = imageRepository.findAllByPlace(place);
        List<String> imageUrlList;
        List<String> requestImages = new ArrayList<>();

        // 버킷에서 이미지 삭제
        for (PlaceImage placeImage : images) {
            if (!placeRequestDto.getExistUrlList().contains(placeImage.getImageUrl())) {
                amazonS3Service.deleteFile(placeImage.getImageUrl().substring(placeImage.getImageUrl().lastIndexOf("/") + 1));
                imageRepository.delete(placeImage);
            }
            else {
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
    public ResponseEntity<String> deletePlace(Long id, UserDetailsImpl userDetails) {

        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.Not_Found_Place);
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
            throw new CustomException(ErrorCode.Not_Found_Place);

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
    public ResponseEntity<Boolean> getPlaceLikeState(Long id, UserDetailsImpl userDetails) {
        Place place = placeRepository.findById(id).orElse(null);
        if (place == null)
            throw new CustomException(ErrorCode.Not_Found_Place);

        boolean state = false;
        if(userDetails != null) {
            Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (member != null) {
                Heart heart = heartRepository.findByMemberAndPlace(member, place).orElse(null);
                if (heart != null) {
                    state = true;
                }
            }
        }
        return ResponseEntity.ok(state);
    }

    //사용자가 찜한 장소들의 리스트 반환
    public ResponseEntity<List<PlaceListResponseDto>> getLikePlaceList(UserDetailsImpl userDetails) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        List<Heart> hearts = heartRepository.findAllByMember(member);

        List<PlaceListResponseDto> responseDtoList = new ArrayList<>();

        for (Heart heart : hearts) {
            Place place = heart.getPlace();
            PlaceImage img = imageRepository.findByPlaceId(place.getId()).orElse(null);
            if (img != null)
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .image(img.getImageUrl())
                        .build());
            else
                responseDtoList.add(PlaceListResponseDto.builder()
                        .id(place.getId())
                        .title(place.getTitle())
                        .star(place.getStar())
                        .build());
        }
        return ResponseEntity.ok(responseDtoList);

    }



























//open api에서 데이터 저장
        @PostConstruct
        public void savePlace () {
            List<Place> places = new ArrayList<>();
            List<PlaceImage> imageList = new ArrayList<>();
            try {
                // parsing할 url 지정(API 키 포함해서)
                StringBuilder url = new StringBuilder("http://apis.data.go.kr/B551011/KorService/areaBasedList");
                url.append("?serviceKey=").append(apiKey);
                url.append("&numOfRows=").append("1000");
                url.append("&pageNo=1");
                url.append("&MobileOS=ETC");
                url.append("&MobileApp=dolphin");
                url.append("&listYN=Y");
                url.append("&arrange=B");
                url.append("&areaCode=39");
                url.append("&sigunguCode=4");


                DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
                Document doc = dBuilder.parse(url.toString());

                // 파싱할 tag
                NodeList nList = doc.getElementsByTagName("item");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    Element eElement = (Element) nNode;

                    if (getTagValue("addr1", eElement).equals(""))
                        continue;

                    Long id = Long.parseLong(getTagValue("contentid", eElement));
                    Place dbPlace = placeRepository.findById(id).orElse(null);
                    if (null == dbPlace) {
                        Place place = Place.builder()
                                .id(Long.parseLong(getTagValue("contentid", eElement)))
                                .address(getTagValue("addr1", eElement))
                                .theme(getTagValue("contenttypeid", eElement))
                                .areaCode(getTagValue("areacode", eElement))
                                .sigunguCode(getTagValue("sigungucode", eElement))
                                .title(getTagValue("title", eElement))
                                .content(getTagValue("content", eElement))
                                .likes(0)
                                .star(0)
                                .mapX(getTagValue("mapx", eElement))
                                .mapY(getTagValue("mapy", eElement))
                                .readCount(Long.parseLong(getTagValue("readcount", eElement)))
                                .build();

                        places.add(place);
                        String img = getTagValue("firstimage", eElement);
                        if (!img.equals(""))
                            imageList.add(PlaceImage.builder()
                                    .place(place)
                                    .imageUrl(img)
                                    .build());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            placeRepository.saveAll(places);
            imageRepository.saveAll(imageList);
            System.out.println("save end");
        }

    }


//    @PostConstruct
//    public void updatePlace(){
//        String[] themes = {"12", "14", "28", "39"};
//        for (String theme: themes) {
//            int i = 1;
//            while (savePlace(theme, i))
//                i++;
//        }
//        System.out.println("end");
//    }
//
//    public boolean savePlace(String theme, int pageNum){
//        List<Place> places = new ArrayList<>();
//        List<PlaceImage> imageList = new ArrayList<>();
//        // 본인이 받은 api키를 추가
//        String key = "";
//        int totalCount = 0;
//        try {
//            // parsing할 url 지정(API 키 포함해서)
//            StringBuilder url = new StringBuilder("http://apis.data.go.kr/B551011/KorService/areaBasedList");
//            url.append("?serviceKey=").append(apiKey);
//            url.append("&numOfRows=").append("7000");
//            url.append("&pageNo=").append(pageNum);
//            url.append("&MobileOS=ETC");
//            url.append("&MobileApp=dolphin");
//            url.append("&listYN=Y");
//            url.append("&arrange=B");
//            url.append("&contentTypeId=").append(theme);
//
//
//            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
//            Document doc = dBuilder.parse(url.toString());
//
//            // 제일 첫번째 태그
//            doc.getDocumentElement().normalize();
//
//            totalCount = Integer.parseInt(doc.getElementsByTagName("totalCount").item(0).getTextContent());
//            // 파싱할 tag
//            NodeList nList = doc.getElementsByTagName("item");
//            for (int temp = 0; temp < nList.getLength(); temp++) {
//                Node nNode = nList.item(temp);
//
//                Element eElement = (Element) nNode;
//
//                if (getTagValue("addr1", eElement).equals(""))
//                    continue;
//
//                Long id = Long.parseLong(getTagValue("contentid", eElement));
//                Place dbPlace = placeRepository.findById(id).orElse(null);
//                if (null == dbPlace) {
//                    StringBuilder url2 = new StringBuilder("http://apis.data.go.kr/B551011/KorService/detailCommon");
//                    url2.append("?serviceKey=" + apiKey);
//                    url2.append("&MobileOS=ETC");
//                    url2.append("&MobileApp=dolphin");
//                    url2.append("&contentId=").append(id);
//                    url2.append("&overviewYN=Y");
//                    url2.append("&contentTypeId=").append(theme);
//
//                    try {
//
//                        DocumentBuilderFactory dbFactoty2 = DocumentBuilderFactory.newInstance();
//                        DocumentBuilder dBuilder2 = dbFactoty2.newDocumentBuilder();
//                        Document doc2 = dBuilder2.parse(url2.toString());
//
//                        // 제일 첫번째 태그
//                        doc2.getDocumentElement().normalize();
//
//                        NodeList nList2 = doc2.getElementsByTagName("item");
//
//                        Node nNode2 = nList2.item(0);
//                        Element eElement2 = (Element) nNode2;
//                        Place place = Place.builder()
//                                .id(Long.parseLong(getTagValue("contentid", eElement)))
//                                .address(getTagValue("addr1", eElement))
//                                .theme(getTagValue("contenttypeid", eElement))
//                                .areaCode(getTagValue("areacode", eElement))
//                                .sigunguCode(getTagValue("sigungucode", eElement))
//                                .title(getTagValue("title", eElement))
//                                .content(getTagValue("content", eElement))
//                                .likes(0)
//                                .star(0)
//                                .mapX(getTagValue("mapx", eElement))
//                                .mapY(getTagValue("mapy", eElement))
////                                .content(getTagValue("overview",eElement2))
//                                .readCount(Long.parseLong(getTagValue("readcount", eElement)))
//                                .build();
//
//                        places.add(place);
//                    String img = getTagValue("firstimage",eElement);
//                    if(!img.equals(""))
//                        imageList.add(PlaceImage.builder()
//                                .place(place)
//                                .imageUrl(img)
//                                .filename(place.getTitle())
//                                .build());
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
//                }
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        placeRepository.saveAll(places);
//        imageRepository.saveAll(imageList);
//        System.out.println("save method end");
//            return totalCount > pageNum * 7000;
//