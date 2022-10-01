package com.dolphin.demo.controller;

import com.dolphin.demo.dto.request.AddPlaceOrderRequestDto;
import com.dolphin.demo.dto.request.OrderRequestDto;
import com.dolphin.demo.dto.request.ImageRequestDto;
import com.dolphin.demo.dto.response.OrderListResponseDto;
import com.dolphin.demo.dto.response.OrderResponseDto;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    //요청 목록 조회
    //    @Secured("ADMIN")
    @GetMapping("/order")
    public ResponseEntity<List<OrderListResponseDto>> getOrderList() {

        return orderService.getOrderList();
    }

    //요청 상세 조회
    //    @Secured("ADMIN")
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {

        return orderService.getOrder(id);
    }


    @PostMapping("/order/{id}")
    public ResponseEntity<OrderResponseDto> createOrder(@PathVariable Long id,
                                                            @Valid @RequestPart(value = "data") OrderRequestDto orderRequestDto,
                                                            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFile,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {


        return orderService.createOrder(id, orderRequestDto, multipartFile, userDetails);
    }


    @PostMapping("/order")
    public ResponseEntity<OrderResponseDto> createAddOrder(
                                                        @Valid @RequestPart(value = "data") AddPlaceOrderRequestDto requestDto,
                                                        @RequestPart(value = "image", required = false) List<MultipartFile> multipartFile,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {


        return orderService.createAddOrder(requestDto, multipartFile, userDetails);
    }

    //    @Secured("ADMIN")
    @PutMapping("/order/{id}")
    public ResponseEntity<Boolean> updateState(@PathVariable Long id){
        return orderService.udateState(id);
    }
}
