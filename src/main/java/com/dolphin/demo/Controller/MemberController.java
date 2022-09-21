package com.dolphin.demo.Controller;

import com.dolphin.demo.domain.Member;
import com.dolphin.demo.dto.requestDto.LoginRequestDto;
import com.dolphin.demo.dto.requestDto.SignupRequestDto;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    //중복 아이디(이메일) 확인
    @PostMapping("/api/member/duplicate")
    public ResponseEntity<String> duplicateUsername(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.duplicateUsername(requestDto);
    }

    //회원가입
    @PostMapping("/api/member/signup")
    public ResponseEntity<Member> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signup(requestDto);
    }

    //로그인
    @PostMapping("/api/member/login")
    public ResponseEntity<Member> login(@RequestBody LoginRequestDto requestDto) {
        return memberService.login(requestDto);
    }

    //로그아웃
    @PostMapping("/api/auth/member/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestHeader(value = "RefreshToken") String refreshToken){
        return memberService.logout(userDetails.getMember().getId(),refreshToken);
    }

    // 만료된 access token 재 발급
    @PostMapping(value = "/api/auth/member/retoken")
    public ResponseEntity<String> reToken(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestHeader(value = "RefreshToken") String refreshToken) {

        return memberService.reToken(accessToken, refreshToken);
    }



}
