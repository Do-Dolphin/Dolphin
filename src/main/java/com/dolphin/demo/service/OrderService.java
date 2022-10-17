package com.dolphin.demo.service;

import com.dolphin.demo.domain.Order;
import com.dolphin.demo.domain.OrderImage;
import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.Place;
import com.dolphin.demo.dto.request.AddPlaceOrderRequestDto;
import com.dolphin.demo.dto.request.OrderRequestDto;
import com.dolphin.demo.dto.request.OrderStateRequestDto;
import com.dolphin.demo.dto.response.OrderListResponseDto;
import com.dolphin.demo.dto.response.OrderResponseDto;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.repository.OrderImageRepository;
import com.dolphin.demo.repository.OrderRepository;
import com.dolphin.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AmazonS3Service amazonS3Service;
    private final OrderImageRepository orderImageRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger("오더 삭제 로그");


    //입력받은 id 요청의 상세 정보를 조회할 수 있는 메서드
    public ResponseEntity<OrderResponseDto> getOrder(Long id) {

        Order order = orderRepository.findById(id).orElse(null);
        if(order == null)
            throw new CustomException(ErrorCode.NOT_FOUND_ORDER);

            List<OrderImage> imageResult = orderImageRepository.findAllByOrder(order);
            List<String> imageList = new ArrayList<>();

            for(OrderImage images : imageResult) {
                imageList.add(images.getImageUrl());
            }

            return ResponseEntity.ok(OrderResponseDto.builder()
                    .id(order.getId())
                    .place_id(order.getPlaceId())
                    .nickname(order.getMember().getNickname())
                    .username(order.getMember().getUsername())
                    .title(order.getTitle())
                    .state(order.isState())
                    .type(order.getType())
                    .answer(order.getAnswer())
                    .content(order.getContent())
                    .imageList(imageList)
                    .createdAt(order.getCreatedAt())
                    .build());
    }

    //요청 리스트를 반환하는 메서드
    public ResponseEntity<List<OrderListResponseDto>> getMyOrderList(UserDetailsImpl userDetails) {
        if (userDetails == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        List<Order> orderList = orderRepository.findAllByMemberOrderByCreatedAtDesc(member);

        List<OrderListResponseDto> orderResult = new ArrayList<>();
        for (Order orders : orderList) {
            OrderListResponseDto orderResponseDto = OrderListResponseDto.builder()
                    .id(orders.getId())
                    .nickname(orders.getMember().getNickname())
                    .title(orders.getTitle())
                    .createdAt(orders.getCreatedAt())
                    .state(orders.isState())
                    .build();
            orderResult.add(orderResponseDto);
        }

        return ResponseEntity.ok().body(orderResult);
    }

    //요청 리스트를 반환하는 메서드
    public ResponseEntity<List<OrderListResponseDto>> getOrderList() {

        List<Order> orderList = orderRepository.findAllByOrderByStateAsc();

        List<OrderListResponseDto> orderResult = new ArrayList<>();
        for (Order orders : orderList) {
            OrderListResponseDto orderResponseDto = OrderListResponseDto.builder()
                    .id(orders.getId())
                    .nickname(orders.getMember().getNickname())
                    .title(orders.getTitle())
                    .createdAt(orders.getCreatedAt())
                    .state(orders.isState())
                    .build();
            orderResult.add(orderResponseDto);
        }

        return ResponseEntity.ok().body(orderResult);
    }

    //수정 및 삭제 관련 요청을 등록하는 메서드
    @Transactional
    public ResponseEntity<OrderResponseDto> createOrder(Long place_id, OrderRequestDto orderRequestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Order order = Order.builder()
                        .content(orderRequestDto.getContent())
                        .title(orderRequestDto.getTitle() + " " + orderRequestDto.getType() + " 요청")
                        .type(orderRequestDto.getType())
                        .member(member)
                        .state(false)
                        .answer("")
                        .placeId(place_id)
                        .build();
        orderRepository.save(order);


        List<String> imageUrlList;
        List<String> imageList = new ArrayList<>();

        imageUrlList = amazonS3Service.upload(multipartFile);
        List<OrderImage> saveImages = new ArrayList<>();
        for (String imageUrl : imageUrlList) {
            OrderImage image = OrderImage.builder()
                    .order(order)
                    .imageUrl(imageUrl)
                    .build();
            saveImages.add(image);
            imageList.add(image.getImageUrl());
        }

        orderImageRepository.saveAll(saveImages);
        return ResponseEntity.ok().body(OrderResponseDto.builder()
                .id(order.getId())
                .place_id(order.getPlaceId())
                .type(order.getType())
                .state(order.isState())
                .nickname(order.getMember().getNickname())
                .answer(order.getAnswer())
                .title(order.getTitle())
                .content(order.getContent())
                .imageList(imageList)
                .createdAt(order.getCreatedAt())
                .build());
    }

    //장소 추가 요청을 생성하는 메서드
    @Transactional
    public ResponseEntity<OrderResponseDto> createAddOrder(AddPlaceOrderRequestDto placeRequestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        StringBuilder content = new StringBuilder();
        content.append("장소 이름: "+placeRequestDto.getTitle()+"\n");
        content.append("장소 설명: "+placeRequestDto.getContent()+"\n");
        content.append("장소 주소: "+placeRequestDto.getAddress()+"\n");

        Order order = Order.builder()
                .content(content.toString())
                .title(placeRequestDto.getTitle()+" 추가 요청")
                .type(placeRequestDto.getType())
                .answer("")
                .member(member)
                .state(false)
                .build();
        orderRepository.save(order);

        List<String> imageUrlList;
        List<String> imageList = new ArrayList<>();

        imageUrlList = amazonS3Service.upload(multipartFile);
        List<OrderImage> saveImages = new ArrayList<>();
        for (String imageUrl : imageUrlList) {
            OrderImage image = OrderImage.builder()
                    .order(order)
                    .imageUrl(imageUrl)
                    .build();
            saveImages.add(image);
            imageList.add(image.getImageUrl());
        }

        orderImageRepository.saveAll(saveImages);
        return ResponseEntity.ok().body(OrderResponseDto.builder()
                .id(order.getId())
                .place_id(order.getPlaceId())
                .type(order.getType())
                .state(order.isState())
                .nickname(order.getMember().getNickname())
                .answer(order.getAnswer())
                .title(order.getTitle())
                .content(order.getContent())
                .imageList(imageList)
                .createdAt(order.getCreatedAt())
                .build());
    }

    //완료한 데이터를 true 상태로 업데이트 해주는 메서드
    @Transactional
    public ResponseEntity<Boolean> udateState(OrderStateRequestDto requestDto) {

        Order order = orderRepository.findById(requestDto.getId()).orElse(null);
        if(order == null)
            throw  new CustomException(ErrorCode.NOT_FOUND_ORDER);

        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);


        order.updateState(true, requestDto.getAnswer());
        notificationService.send(member, requestDto.getAnswer());

        return ResponseEntity.ok().body(order.isState());
    }


    //open api에서 유효하지 않은 데이터를 걸러서 order로 생성
    @Transactional
    public void createApiOrder(Place place, String title, String image) {

        Member member = memberRepository.findByNickname("admin").orElse(null);

        StringBuilder content = new StringBuilder();
        content.append("장소 이름: "+place.getTitle()+"\n");
        content.append("장소 설명: "+place.getContent()+"\n");
        content.append("장소 주소: "+place.getAddress()+"\n");
        content.append("지역 코드: "+place.getAreaCode()+"\n");
        content.append("시군구 코드: "+place.getSigunguCode()+"\n");
        content.append("장소 이미지: "+image+"\n");

        Order order = Order.builder()
                .content(content.toString())
                .title(title)
                .type("추가")
                .member(member)
                .state(false)
                .answer("")
                .build();
        orderRepository.save(order);


        if(!image.equals("")){
            orderImageRepository.save( OrderImage.builder()
                    .order(order)
                    .imageUrl(image)
                    .build());

        }
    }


    //한 달에 한 번 처리된 요청을 삭제하는 메서드
    @Scheduled(cron = "0 0 0 1 * ?")
    public void deleteOrder(){
        logger.info(new Date() + " 스케쥴러 실행");
        List<Order> orders = orderRepository.findAllByStateTrue();
        for (Order order : orders) {
            logger.info("Delete Order : "+order.getId());
        }
        orderRepository.deleteAll(orders);
    }
}
