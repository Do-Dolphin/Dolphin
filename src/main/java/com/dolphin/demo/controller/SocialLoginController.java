package com.dolphin.demo.controller;

import com.dolphin.demo.service.KakaoLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final KakaoLoginService kakaoLogin;

    @GetMapping("/api/kakao/login")
    public ResponseEntity<String> kakaoLogin(
            @RequestParam(value = "code") String code) throws JsonProcessingException {
        return kakaoLogin.kakaoLogin(code);
    }

    //https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=1af8a4039402102e8193e16de2a1b4fb&redirect_uri=http://localhost:3000/oauth/callback/kakao

}
