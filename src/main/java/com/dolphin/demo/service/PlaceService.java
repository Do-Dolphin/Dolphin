package com.dolphin.demo.service;

import com.dolphin.demo.domain.Comment;
import com.dolphin.demo.domain.Image;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.response.PlaceListResponseDto;
import com.dolphin.demo.dto.response.PlaceResponseDto;
import com.dolphin.demo.dto.response.RandomPlaceResponseDto;
import com.dolphin.demo.repository.ImageRepository;
import com.dolphin.demo.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final ImageRepository imageRepository;
    @Value("${restAPI.key}")
    String apiKey;

    public ResponseEntity<List<PlaceListResponseDto>> getPlace(String theme, String areaCode, String sigunguCode, String pageNum) {
        List<PlaceListResponseDto> responseDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum), 10);

        List<Place> placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode,sigunguCode,theme,pageRequest);
        for (Place place : placeList) {
            System.out.println(place.getId());
            Image img = imageRepository.findByPlaceId(place.getId());
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

    public ResponseEntity<RandomPlaceResponseDto> randomPlace(){
        List<PlaceListResponseDto> randomList = new ArrayList<>();
        int areaCode = (int) (Math.random() * 17 + 1);
        int sigunguCode;
        String area;
            if (areaCode > 8) {
                areaCode += 22;

                while (true) {
                    sigunguCode = (int) (Math.random() * 31 + 1);

                    if (placeRepository.existsByAreaCodeAndSigunguCode(String.valueOf(areaCode), String.valueOf(sigunguCode)))
                        break;
                }
                randomList.add(randomSigungu(String.valueOf(areaCode),String.valueOf(sigunguCode),"12"));
                randomList.add(randomSigungu(String.valueOf(areaCode),String.valueOf(sigunguCode),"14"));
                randomList.add(randomSigungu(String.valueOf(areaCode),String.valueOf(sigunguCode),"28"));
                randomList.add(randomSigungu(String.valueOf(areaCode),String.valueOf(sigunguCode),"39"));
                area =getArea(randomList.get(0),1);
            } else{
                randomList.add(randomArea(String.valueOf(areaCode),"12"));
                randomList.add(randomArea(String.valueOf(areaCode),"14"));
                randomList.add(randomArea(String.valueOf(areaCode),"28"));
                randomList.add(randomArea(String.valueOf(areaCode),"39"));
                area =getArea(randomList.get(0),0);
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
        if(null != nlList.item(0) && nlList.item(0).getChildNodes().item(0) != null)
            result = nlList.item(0).getChildNodes().item(0).getTextContent();

        return result;
    }

    public PlaceListResponseDto randomArea(String areaCode, String theme){

        List<Place>placeList = placeRepository.findAllByAreaCodeAndTheme(String.valueOf(areaCode), theme);

        int index = (int) (Math.random() * placeList.size());
        Place place = placeList.get(index);
        Image img = imageRepository.findByPlaceId(place.getId());
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

    public String getArea(PlaceListResponseDto responseDto, int n){
        Place place = placeRepository.findById(responseDto.getId()).orElse(null);
        StringBuilder s = new StringBuilder();
        s.append(place.getAddress().split(" ")[0]);
        for (int i = 1; i <= n; i++) {
            s.append(" ");
            s.append(place.getAddress().split(" ")[n]);
        }
        return s.toString();
    }

    public PlaceListResponseDto randomSigungu(String areaCode,String sigungu, String theme){

        List<Place>placeList = placeRepository.findAllByAreaCodeAndSigunguCodeAndTheme(areaCode, sigungu, theme);

        int index = (int) (Math.random() * placeList.size());
        Place place = placeList.get(index);

        Image img = imageRepository.findByPlaceId(place.getId());
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


















    @PostConstruct
    public void updatePlace(){
        int i = 1;
        while (savePlace("12",i))
            i++;
        i = 1;
        while (savePlace("14",i))
            i++;
        i = 1;
        while (savePlace("28",i))
            i++;
        i = 1;
        while (savePlace("39",i))
            i++;
        System.out.println("end");
    }

    public boolean savePlace(String theme, int pageNum){
        List<Place> places = new ArrayList<>();
        List<Image> imageList = new ArrayList<>();
        // 본인이 받은 api키를 추가
        String key = "";
        int totalCount = 0;
        try {
            // parsing할 url 지정(API 키 포함해서)
            StringBuilder url = new StringBuilder("http://apis.data.go.kr/B551011/KorService/areaBasedList");
            url.append("?serviceKey=").append(apiKey);
            url.append("&numOfRows=").append("7000");
            url.append("&pageNo=").append(pageNum);
            url.append("&MobileOS=ETC");
            url.append("&MobileApp=dolphin");
            url.append("&listYN=Y");
            url.append("&arrange=B");
            url.append("&contentTypeId=").append(theme);


            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            Document doc = dBuilder.parse(url.toString());

            // 제일 첫번째 태그
            doc.getDocumentElement().normalize();

            totalCount = Integer.parseInt(doc.getElementsByTagName("totalCount").item(0).getTextContent());
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
//                                .content(getTagValue("overview",eElement2))
                                .readCount(Long.parseLong(getTagValue("readcount", eElement)))
                                .build();

                        places.add(place);
                    String img = getTagValue("firstimage",eElement);
                    if(!img.equals(""))
                        imageList.add(Image.builder()
                                .place(place)
                                .imageUrl(img)
                                .filename(place.getTitle())
                                .build());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        placeRepository.saveAll(places);
        imageRepository.saveAll(imageList);
        System.out.println("save method end");
            return totalCount > pageNum * 7000;
        }
}
