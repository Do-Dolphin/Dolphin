package com.dolphin.demo.service;

import com.dolphin.demo.domain.Member;
import com.dolphin.demo.dto.request.LoginRequestDto;
import com.dolphin.demo.dto.request.SignupRequestDto;
import com.dolphin.demo.jwt.JwtTokenProvider;
import com.dolphin.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisService redisService;

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<String> duplicateUsername(LoginRequestDto requestDto) {
        System.out.println(requestDto.getUsername());
        if (memberRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        }
        String massege = "사용가능한 이메일입니다.";

        return new ResponseEntity<>(massege, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Member> signup(SignupRequestDto requestDto) {
        System.out.println(requestDto.getUsername());
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (memberRepository.existsByUsername((requestDto.getUsername()))) {
            throw new IllegalArgumentException("아이디 중복체크는 필수입니다.");
        }

//        // 인증 메일 전송
//        emailConfirmTokenService.createEmailConfirmationToken(requestDto.getUsername());

        Member member = Member.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .build();
        memberRepository.save(member);

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    public ResponseEntity<String> logout(Long memberId, String refreshToken) {
        memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("로그아웃 실패")
        );
        redisService.deleteValues(refreshToken);
        String message = "로그아웃되었습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Member> login(LoginRequestDto requestDto) {
        System.out.println(requestDto.getUsername());
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 확인해 주세요");
        }

        //토큰 만들기
        tokensProcess(member.getUsername());

        return new ResponseEntity<>(member,HttpStatus.OK);
    }

    public void tokensProcess(String username) {
        String refreshToken = jwtTokenProvider.createRefreshToken();
        redisService.setValues(refreshToken, username);
        redisService.setExpire(refreshToken, 7 * 24 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS);
        jwtTokenProvider.createToken(username);
    }

    // access token 만료시 재발급
    @Transactional
    public ResponseEntity<String> reToken(String accessToken, String refreshToken) {
        // accessToken 만료 기간 확인
        if (jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("토큰 기간이 만료되지 않아서 갱신되지 않습니다");
        }

        // RefreshToken 유효성 검사
        String token = refreshToken.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("refresh token 기간이 만료 되었습니다.");
        }

        // Redis에서 refreshToken 유저 정보 꺼내기
        String username = redisService.getValues(token);
        if (username == null) {
            throw new IllegalArgumentException("토큰 정보가 없습니다.");
        }

        // 토큰 재발행
        tokensProcess(username);
        // 기존 토큰 삭제
        redisService.deleteValues(token);

        String message = "token 갱신 완료";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }




}
