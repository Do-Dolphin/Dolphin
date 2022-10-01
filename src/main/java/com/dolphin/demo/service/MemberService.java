package com.dolphin.demo.service;

import com.dolphin.demo.domain.Member;
import com.dolphin.demo.domain.MemberRoleEnum;
import com.dolphin.demo.dto.request.LoginRequestDto;
import com.dolphin.demo.dto.request.NicknameDto;
import com.dolphin.demo.dto.request.SignupRequestDto;
import com.dolphin.demo.jwt.JwtTokenProvider;
import com.dolphin.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisService redisService;

    private final MemberRepository memberRepository;

    @Transactional
    public ResponseEntity<String> signup(SignupRequestDto requestDto) {
        System.out.println(requestDto.getUsername());
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (memberRepository.existsByUsername((requestDto.getUsername()))) {
            throw new IllegalArgumentException("아이디 중복체크는 필수입니다.");
        }

        String nickname = rendomNickname();
        while (null != memberRepository.findByNickname(nickname).orElse(null)){
            nickname = rendomNickname();
        }
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(nickname)
                .role(MemberRoleEnum.MEMBER)
                .build();
        memberRepository.save(member);

        return new ResponseEntity<>(member.getNickname()+"님 회원가입을 환영합니다.", HttpStatus.OK);
    }

    public ResponseEntity<String> logout(Long memberId, String refreshToken) {
        memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("로그아웃 실패")
        );

        String[] token = refreshToken.split(" ");
        redisService.deleteValues(token[1]);

        return new ResponseEntity<>("로그아웃되었습니다.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 확인해 주세요");
        }

        //토큰 만들기
        tokensProcess(member.getUsername());

        return new ResponseEntity<>(member.getNickname()+"님 방문을 환영합니다.",HttpStatus.OK);
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

        return new ResponseEntity<>("token 갱신 완료", HttpStatus.OK);
    }

    public String rendomNickname() {
        List<String> location = Arrays.asList( "운동장에서 ", "바닷가에서 ", "게더에서 ", "회식자리에서 ", "방안에서 ", "거실에서 ", "화장실에서 ", "의자에서 ", "캠핑장에서 ",
                "책상위에서 ", "교실바닥에서 ", "사물함위에서 ", "옥상달빛아래에서 ", "계단층계에서 ", "인터넷에서 ", "다리위에서 ", "굴다리아래에서 ", "태양아래에서 ", "고깃집안에서 ",
                "라멘집에서 ", "식당안에서 ", "자판기앞에서 ","부대안에서 ","콘서트장에서 ", "무대위에서 ", "무대아래에서 ", "비행기안에서 ", "경복궁안에서 ", "협곡아래에서 ",
                "외나무다리에서 ", "이글루위에서 ", "교회안에서 ", "종탑위에서 ", "초원에서 ","스키장에서 ", "리프트위에서 ", "비행기날개위에서 ", "동물원에서 ", "원숭이우리에서 ","장난감집에서 ",
                "목장에서 ", "나무아래에서 ", "나뭇가지위에서 ", "책상아래에서 ", "현관앞에서 ", "칠판앞에서 ", "에어컨위에서 ", "설산위에서 ", "구름위에서 ", "폭설속에서 ",
                "그리움속에서 ", "바다위에서 ", "아파트옥상에서 ", "학교옥상에서 ", "체육관무대에서 ", "코트에서 ", "사바리초원에서 ", "추억속에서 ", "남극에서 ", "북극에서 ",
                "리듬속에서 ","도서관에서 ", "빙판위에서 ", "산아래에서 ", "무리속에서 ", "비내리는신호등아래에서 ", "신호등아래에서 ","바람속에서 ",
                "눈물속에서 ", "헬스장에서 ", "사우나실에서 ", "골짝기에서 ", "서부에서 ", "처마밑에서 ", "김밥집앞에서 ", "모두의관심속에서 ", "서랍장에서 ", "번지점프대앞에서 ",
                "숲속에서");

        List<String> action = Arrays.asList("국수먹는 ","고백하는 ","노래하는 ","춤추는 ","샅샅이살피는 ","사과하는 ","사과먹는 ","영화보는 ","추억하는 ","여행하는 ","코딩하는 ",
                "라면먹는 ","바라보는 ", "추억하는 ", "스텝밟는 ", "앞사람을따라가는 ", "도약하는 ", "만화책을보는 ", "게임을하는 ", "벨튀를하는 ", "크게소리치는 ","가슴을두드리는 ",
                "가오잡는 ", "운동하는 ", "너에게달려가는 ", "화장실찾는 ", "잠잘준비하는 ", "도망가는 ", "셔플추는 ", "쫒아가는 ", "핸드폰하는 ", "편지쓰는 ", "피아노치는 ",
                "폼잡는 ", "점프하는 ", "기도하는 ", "떡먹는 ", "시를짓는 ", "좀비잡는 ", "쇼핑하는 ", "응원하는 ", "웃음짓는 ", "째려보는 ", "텝댄스추는 ", "야근하는 ", "보드점프하는 ",
                "보드게임하는 ", "주사위던지는 ", "멋있는 척하는 ", "팔굽혀펴기하는 ", "스쿼트하는 ", "넘어지고누운척하는 ", "한사람만사랑하는 ", "윈드밀치는 ", "니체를좋아하는 ",
                "짜장면을비비는 ", "날개짓하는 ", "화장하는 ", "과자봉지를뜯는 ", "기부하는 ", "테트리스하는 ", "입대하는 ", "새시계를자랑하는 ", "금니빼고다씹어먹는 ", "아직한발남아있는",
                "방목하는 ", "저작권에걸리는 ", "검정티를입는 ", "모델워킹하는 ", "파워워킹하는 ", "수다떠는 ", "경례하는 ", "더블클릭때문에화가나는 ", "새마우스를구매하는 ",
                "컵에물따르는 ", "안약넣는 ", "뜀걸음하는 ", "나잡아봐라하는 ", "사장의말을무시하는 ", "이름짓는 ", "우산을피는 ", "기다리는 ",
                "무언가를기대하는 ", "졸아버리는 ", "담력훈련하는 ", "철봉하는 ", "말이없는 ", "다정하게쳐다보는 ", "다정하게맞아주는 ", "웃으면서도망가는 ", "리모콘을찾는 ",
                "요리하는 ", "비내리길기원하는 ", "스케이트타는 ", "음료를뽑는 ", "타자치는 ", "비를피하는 ", "태양을피하는 ", "태양을피하고싶은 ", "양을치는 ", "눈사람만드는 ",
                "종을치는 ", "종칠준비를하는 ", "사자와달리기하는 ", "하이에나와달리기하는 ", "스키타는 ", "미끄러지는 ", "미끄럼틀타는 ", "먹이주는 ", "임을생각하는 ", "너무자고싶은 ",
                "숨어있는 ", "열쇠찾는 ", "낙서하는 ", "날개짓하는 ", "이슬을피하는 ", "끝없이코딩하는", "전이먹고싶은", "회가먹고싶은", "파파야가먹고싶은", "두리안이먹고싶은 ","거절하는",
                "숙제하는 ", "보물찾는 ", "이모를찾는 ", "과자먹는 ", "몰래자러가는 ", "온도재는 ", "방송하는 ", "헤엄치는 ", "자랑하는 ", "네모네모한", "귀여움을뽑내는 ", "꿈틀거리는 ",
                "셀카찍는 ", "사진찍는 ", "드러눕는 ", "청소하는 ", "간식먹는 ", "광합성하는 ", "굴러다니는 ", "활기찬 ", "음침한 ", "신호지키는 ", "손을들고건너는 ", "고스톱치는 ",
                "행복한 ", "떠드는 ", "다리떠는 ", "꿈꾸는 ", "즐겁게춤추는 ", "즐거운 ", "캐리어를끄는 ");

        List<String> anyone = Arrays.asList("곰돌이", "냥이", "고양이", "고양희", "토끼", "레드토끼", "미역", "전복", "사자", "자라", "민들레", "군돌이", "해바라기 ", "치타",
                "표범", "사슴", "숫사슴", "강아지", "고래", "돌고래", "고뤠", "오리", "거위", "닭", "병아리", "짹짹이", "하마", "기린", "용씨", "냥아치", "늑대", "여우", "황소",
                "매", "양", "염소", "나무", "바람", "말", "태양", "씨앗", "풀잎", "개발자", "고먐미", "클로버", "주작", "현무", "거북이", "펭귄", "북금곰", "수달", "꺼부기", "전기쥐",
                "햄스터", "랍스터", "타조", "공작새", "비둘기", "뱁새", "제비", "까치", "꿩", "송골매", "폭탄광", "스파이", "마피아", "반장", "요리사", "탐험가", "비버", "도마뱀","앵무새",
                "쿼카", "친칠라", "해파리", "고구마", "감자", "이삭", "빵순이", "오소리", "완두콩", "지렁이", "삽살개", "수학자", "과학자", "선생님", "변호사" );


        Collections.shuffle(location);
        Collections.shuffle(action);
        Collections.shuffle(anyone);
        return location.get(0)+action.get(0)+anyone.get(0);
        }

    //닉네임 변경
    @Transactional
    public ResponseEntity<String> updateNickname(Member memberinfo, NicknameDto nicknameDto) {
        Member member = memberRepository.findByUsername(memberinfo.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
        member.updateNickname(nicknameDto);

        return new ResponseEntity<>("닉네임이 "+member.getNickname()+"로 변경되었습니다.",HttpStatus.OK);
    }

    //비밀번호 변경
    @Transactional
    public ResponseEntity<String> updatePassword(Member memberinfo, NicknameDto nicknameDto) {
        Member member = memberRepository.findByUsername(memberinfo.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
        if(passwordEncoder.matches(member.getPassword(), nicknameDto.getPassword())){
            return new ResponseEntity<>("아이디와 비밀번호가 같지않습니다.",HttpStatus.OK);
        }
        if (!nicknameDto.getNewPassword().equals(nicknameDto.getNewPasswordConfirm())) {
            return new ResponseEntity<>("새로운 비번이 동일하지 않습니다.",HttpStatus.OK);
        }

        member.updatePassword(nicknameDto);

        return new ResponseEntity<>("패스워드가 성공적으로 변경되었습니다.",HttpStatus.OK);
    }
}





