package com.dolphin.demo.controller;

import com.dolphin.demo.dto.request.LoginRequestDto;
import com.dolphin.demo.dto.request.MemberOutDto;
import com.dolphin.demo.dto.request.NicknameDto;
import com.dolphin.demo.dto.request.SignupRequestDto;
import com.dolphin.demo.dto.response.MemberResponseDto;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    
    //회원가입
    @PostMapping("/api/member/signup")
    public ResponseEntity<MemberResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signup(requestDto);
    }

    //로그인
    @PostMapping("/api/member/login")
    public ResponseEntity<MemberResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return memberService.login(requestDto);
    }

    //로그아웃
    @PostMapping("/api/auth/member/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestHeader(value = "RefreshToken") String refreshToken){
        return memberService.logout(userDetails.getMember().getId(),refreshToken);
    }

    //닉네임 변경
    @PutMapping("/api/auth/member/updatenickname")
    public ResponseEntity<MemberResponseDto> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody NicknameDto nicknameDto) {
        return memberService.updateNickname(userDetails.getMember(), nicknameDto);
    }

    //비밀번호 변경
    @PutMapping("/api/auth/member/updatepassword")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody NicknameDto nicknameDto) {
        return memberService.updatePassword(userDetails.getMember(), nicknameDto);
    }

    // 만료된 access token 재 발급
    @PostMapping(value = "/api/auth/member/retoken")
    public ResponseEntity<String> reToken(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestHeader(value = "RefreshToken") String refreshToken) {

        return memberService.reToken(accessToken, refreshToken);
    }

    @DeleteMapping("/api/auth/member/memberout")
    public ResponseEntity<String> memberout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody MemberOutDto memberOutDto){
        return memberService.memberout(userDetails.getMember(), memberOutDto);
    }

}
