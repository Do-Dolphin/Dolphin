package com.dolphin.demo.service;

import com.dolphin.demo.domain.Order;
import com.dolphin.demo.domain.OrderImage;
import com.dolphin.demo.domain.Member;
import com.dolphin.demo.dto.request.AddPlaceOrderRequestDto;
import com.dolphin.demo.dto.request.OrderRequestDto;
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

    private static final Logger logger = LoggerFactory.getLogger("오더 삭제 로그");


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
                    .title(order.getTitle())
                    .state(order.isState())
                    .type(order.getType())
                    .content(order.getContent())
                    .imageList(imageList)
                    .createdAt(order.getCreatedAt())
                    .build());
    }

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

    @Transactional
    public ResponseEntity<OrderResponseDto> createOrder(Long place_id, OrderRequestDto orderRequestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Order order = Order.builder()
                        .content(orderRequestDto.getContent())
                        .title(orderRequestDto.getTitle())
                        .type(orderRequestDto.getType())
                        .member(member)
                        .state(false)
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
                .title(order.getTitle())
                .content(order.getContent())
                .imageList(imageList)
                .createdAt(order.getCreatedAt())
                .build());
    }

    @Transactional
    public ResponseEntity<OrderResponseDto> createAddOrder(AddPlaceOrderRequestDto placeRequestDto, List<MultipartFile> multipartFile, UserDetailsImpl userDetails) throws IOException {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        StringBuilder content = new StringBuilder();
        content.append("장소 이름: "+placeRequestDto.getTitle()+"\n");
        content.append("장소 설명: "+placeRequestDto.getContent()+"\n");
        content.append("장소 주소: "+placeRequestDto.getAddress()+"\n");
        content.append("장소 테마: "+placeRequestDto.getTheme()+"\n");

        Order order = Order.builder()
                .content(content.toString())
                .title(placeRequestDto.getTitle()+" 생성 요청")
                .type("추가")
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
                .title(order.getTitle())
                .content(order.getContent())
                .imageList(imageList)
                .createdAt(order.getCreatedAt())
                .build());
    }

    @Transactional
    public ResponseEntity<Boolean> udateState(Long id) {

        Order order = orderRepository.findById(id).orElse(null);
        //나중에 exception 만들면 수정
        if(order == null)
            throw  new CustomException(ErrorCode.NOT_FOUND_ORDER);

        order.updateState(order.isState());

        return ResponseEntity.ok().body(order.isState());
    }



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
