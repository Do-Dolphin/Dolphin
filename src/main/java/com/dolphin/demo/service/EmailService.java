package com.dolphin.demo.service;

import com.dolphin.demo.dto.request.LoginRequestDto;
import com.dolphin.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    private final RedisService redisService;

    private final MemberRepository memberRepository;


    @Transactional
    public ResponseEntity<String> authEmail(LoginRequestDto requestDto) {
        if (memberRepository.existsByUsername(requestDto.getUsername())) {
            return new ResponseEntity<>("중복 이메일입니다.",HttpStatus.OK);
        }
        // 인증코드 생성
        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        //메일보내기메서드 실행
        sendAuthEmail(requestDto.getUsername(), authKey);
        return new ResponseEntity<>("이메일이 전송되었습니다. 코드만료시간은 5분입니다." ,HttpStatus.OK);
    }

    private void sendAuthEmail(String email, String authKey) {

        // 이메일 제목 작성
        String subject = "[돌고돌래] 인증 메일입니다";
        // 이메일 내용 작성
        String text = "";
        text += "<div style='margin:100px;'>";
        text += "<h1> 안녕하세요</h1>";
        text += "<h1> 당신의 발걸음과 함께하는 돌고돌래 입니다</h1>";
        text += "<br>";
        text += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        text += "<br>";
        text += "<p>당신의 걸음걸음을 언제나 응원합니다. 감사합니다!<p>";
        text += "<br>";
        text += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        text += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        text += "<div style='font-size:130%'>";
        text += "CODE : <strong>";
        text +=  authKey + "</strong><div><br/> "; // 메일에 인증번호 넣기
        text += "</div>";

        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        //redis에 5분 설정으로 authkey(key), email(value)저장
        redisService.setValuesExpire(authKey, email, 60*5L);
    }

    public ResponseEntity<String> authEmailCode(String code) {
        // 입력 받은 code(key)를 이용해 email(value)을 꺼낸다.
        String email = redisService.getValues(code);

        // email이 존재하지 않으면, 유효 기간 만료이거나 코드 잘못 입력
        if (email == null) {
            return new ResponseEntity<>("코드가 잘못입력되었거나 유효기간이 만료되었습니다",HttpStatus.OK);
        }

        return new ResponseEntity<>("인증되었습니다.", HttpStatus.OK);
    }
}