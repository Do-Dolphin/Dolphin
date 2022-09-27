package com.dolphin.demo.service;

import com.dolphin.demo.domain.PlaceImage;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.PlaceRequestDto;
import com.dolphin.demo.dto.response.PlaceListResponseDto;
import com.dolphin.demo.dto.response.PlaceResponseDto;
import com.dolphin.demo.dto.response.RandomPlaceResponseDto;
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
    @Value("${restAPI.key}")
    String apiKey;

    public ResponseEntity<List<PlaceListResponseDto>> getPlace(String theme, String areaCode, String sigunguCode, String pageNum) {
        List<PlaceListResponseDto> responseDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum), 10);

        List<Place> placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode, sigunguCode, theme, pageRequest);
        for (Place place : placeList) {
            System.out.println(place.getId());
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

            for (String theme: themes) {
                randomList.add(randomSigungu(String.valueOf(areaCode), String.valueOf(sigunguCode), theme));
            }
            area =getArea(randomList.get(0),1);

        } else{
            for (String theme: themes) {

                randomList.add(randomArea(String.valueOf(areaCode), theme));
            }
            area = getArea(randomList.get(0), 0);
        }


        return ResponseEntity.ok(RandomPlaceResponseDto.builder()
                .placeList(randomList)
                .area(area)
                .build());
    }

    public String getTagValue(String tag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag);
        if (null != nlList.item(0) && nlList.item(0).getChildNodes().item(0) != null)
            result = nlList.item(0).getChildNodes().item(0).getTextContent();

        return result;
    }

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



    public List<PlaceListResponseDto> getRank(int theme){
        PageRequest pageRequest = PageRequest.of(1, 10);
        List<PlaceListResponseDto> responseDtoList = new ArrayList<>();
        List<Place> placeList = placeRepository.findAllByThemeOrderByReadCountDesc(String.valueOf(theme),pageRequest);
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

    public ResponseEntity<PlaceResponseDto> getPlaceDetail(Long id) {

        Place place = placeRepository.findById(id).orElse(null);

        if(place == null)
            return ResponseEntity.notFound().build();

        List<PlaceImage> img = imageRepository.findAllByPlaceId(place.getId());
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

    @Transactional
    public ResponseEntity<PlaceResponseDto> createPlace(PlaceRequestDto requestDto, List<MultipartFile> multipartFile) throws IOException {
        Long id = placeRepository.getTopByOrderByIdDesc().getId();
        if(id < 5000000)
            id = 4999999L;
        Place place = Place.builder()
                .id(id+1)
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


//    public ResponseEntity<PlaceResponseDto> updatePlace(Long id, PlaceRequestDto placeRequestDto){
//        Place place = placeRepository.findById(id).orElse(null);
//        if(place == null)
//            return ResponseEntity.notFound().build();
//
//        place.update(placeRequestDto);
//
//        return ResponseEntity.ok().body(PlaceResponseDto.builder()
//                .id(place.getId())
//                .title(place.getTitle())
//                .content(place.getContent())
//                .address(place.getAddress())
//                .star(place.getStar())
//                .theme(place.getTheme())
//                .likes(place.getLikes())
//                .mapX(place.getMapX())
//                .mapY(place.getMapY())
//                .imageUrl(imageList)
//                .build());
//    }





















    @PostConstruct
    public void savePlace() {
        List<Place> places = new ArrayList<>();
        List<PlaceImage> imageList = new ArrayList<>();
        // 본인이 받은 api키를 추가
        String key = "";
        int totalCount = 0;
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

            // 제일 첫번째 태그
            doc.getDocumentElement().normalize();

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
    }}


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