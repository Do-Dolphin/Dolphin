package com.dolphin.demo.controller;

import com.dolphin.demo.dto.request.AuthEmailRequestDto;
import com.dolphin.demo.dto.request.LoginRequestDto;
import com.dolphin.demo.service.EmailService;
import com.dolphin.demo.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class EmailController {

    private final EmailService emailService;

    // 이메일 인증 코드 보내기
    @PostMapping("/api/member/email")
    public ResponseEntity<String> authEmail(@RequestBody LoginRequestDto request){
        return emailService.authEmail(request);
    }

    // 인증코드 검사
    @PostMapping("/api/member/codeEmail")
    public ResponseEntity<Boolean> authCodeEmail(@RequestBody AuthEmailRequestDto requestDto){
        return emailService.authEmailCode(requestDto);
    }

}
