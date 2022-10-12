package com.dolphin.demo.service;

import com.dolphin.demo.domain.Place;
import com.dolphin.demo.domain.PlaceImage;
import com.dolphin.demo.repository.PlaceImageRepository;
import com.dolphin.demo.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ApiService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository imageRepository;
    private final PlaceService placeService;
    private final OrderService orderService;
    @Value("${restAPI.key}")
    String apiKey;


    private static final Logger logger = LoggerFactory.getLogger("ApiService");

    //태그에 해당하는 값 추출
    public String getTagValue(String tag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag);
        if (null != nlList.item(0) && nlList.item(0).getChildNodes().item(0) != null)
            result = nlList.item(0).getChildNodes().item(0).getTextContent();

        return result;
    }


    public void content(String key){
        int i =0;
        List<Place> places = placeRepository.findAllByContent("", PageRequest.of(0, 100));
        for (Place place : places) {
            StringBuilder url = new StringBuilder("http://apis.data.go.kr/B551011/KorService/detailCommon");
            url.append("?serviceKey=" + key);
            url.append("&MobileOS=ETC");
            url.append("&MobileApp=dolphin");
            url.append("&contentId=").append(place.getId());
            url.append("&overviewYN=Y");

            try {

                DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
                Document doc = dBuilder.parse(url.toString());

                // 제일 첫번째 태그
                doc.getDocumentElement().normalize();
                if(doc.getElementsByTagName("errMsg").item(0)!=null) {
                    logger.info(doc.getElementsByTagName("errMsg").item(0).getTextContent());
                    continue;
                }

                NodeList nList = doc.getElementsByTagName("item");
                Node nNode = nList.item(0);
                Element eElement = (Element) nNode;
                placeService.updateContent(place.getId(), getTagValue("overview", eElement));
                logger.info(place.getId() + " 상세 설명 저장 완료");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void addImage() {
        content(apiKey);

        add(apiKey);
    }

    @Transactional
    public void add(String key){
        List<PlaceImage> images = imageRepository.findAllByStateFalse(PageRequest.of(0, 1000));
        List<PlaceImage> imageList = new ArrayList<>();
        for (PlaceImage placeImage : images) {
           placeService.updateState(placeImage.getId(), true);
            Place place = placeImage.getPlace();
            StringBuilder url = new StringBuilder("http://apis.data.go.kr/B551011/KorService/detailImage");
            url.append("?serviceKey=" + key);
            url.append("&MobileOS=ETC");
            url.append("&MobileApp=dolphin");
            url.append("&contentId=").append(place.getId());
            url.append("&subImageYN=Y");

            try {

                DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
                Document doc = dBuilder.parse(url.toString());

                NodeList nList = doc.getElementsByTagName("item");
                for (int i = 0; i < nList.getLength(); i++) {

                    Node nNode = nList.item(i);
                    Element eElement = (Element) nNode;

                    String imgUrl = getTagValue("originimgurl", eElement);
                    imageList.add(PlaceImage.builder()
                            .place(place)
                            .imageUrl(imgUrl)
                            .state(true)
                            .build());
                }
                imageRepository.saveAll(imageList);
                logger.info(place.getId() + " 이미지 저장 완료 ");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        imageRepository.saveAll(imageList);
        logger.info(imageList.size() + "개의 이미지 추가됨");
    }


    @Scheduled(cron = "0 40 14 12 * ?")
    @Transactional
    public void updatePlace() {
        String[] themes = {"12", "14", "28", "39"};
        for (String theme : themes) {
            int i = 1;
            while (savePlace(theme, i))
                i++;
            logger.info("테마 : " + theme + " 저장 완료");
        }
        logger.info("모든 데이터 저장 완료");
    }



    public boolean savePlace(String theme, int pageNum) {
        List<Place> places = new ArrayList<>();
        List<PlaceImage> imageList = new ArrayList<>();
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

            totalCount = Integer.parseInt(doc.getElementsByTagName("totalCount").item(0).getTextContent());
            // 파싱할 tag
            NodeList nList = doc.getElementsByTagName("item");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;
                Long id = Long.parseLong(getTagValue("contentid", eElement));
                if (!placeRepository.existsById(id)) {
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

                    String img = getTagValue("firstimage", eElement);
                    if(place.getAddress().equals("")){
                        orderService.createApiOrder(place, "주소값 누락", img);
                        continue;
                    }
                    if(place.getAreaCode().equals("")){
                        orderService.createApiOrder(place, "지역코드 누락", img);
                        continue;
                    }
                    if(Integer.parseInt(place.getAreaCode()) > 8 && place.getSigunguCode().equals("")){
                        orderService.createApiOrder(place, "시군구코드 누락", img);
                        continue;
                    }
                    if(Integer.parseInt(place.getMapX()) == 0 || Integer.parseInt(place.getMapX()) >= 132|| Integer.parseInt(place.getMapX()) == 0 || Integer.parseInt(place.getMapX()) >= 39){
                        orderService.createApiOrder(place, "유효하지 않은 좌표값", img);
                        continue;
                    }
                    if (!img.equals(""))
                        imageList.add(PlaceImage.builder()
                                .place(place)
                                .imageUrl(img)
                                .state(false)
                                .build());
                    places.add(place);
                    logger.info(place.getId() + " 저장 완료");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        placeRepository.saveAll(places);
        imageRepository.saveAll(imageList);
        logger.info(places.size() + "개 저장 완료");
        return totalCount > pageNum * 7000;

    }


}
