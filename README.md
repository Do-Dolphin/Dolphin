<p align="center">
 <img src="https://user-images.githubusercontent.com/97495661/198329036-9b258aa5-cb1b-4d27-b8c6-7609e052f861.png">
</p>
 <h3> 어디로 갈지 고민 될 때! 랜덤 여행지 추천부터, 수많은 여행지를 소개해드리는 서비스입니다. </h3></div>
<div align="center"></div>

# 🐬 돌고돌래

- [프론트엔드 GitHub](https://github.com/Greendeww/dolgo-dolrae)
- [팀노션](https://www.notion.so/1-695787ebec1e4ecd91a12ff8ae70f7b7)
- [사이트 바로가기](http://dolgo.site/)
---
### 📌 프로젝트 소개
- 여행지 추천 서비스

  - 관광, 관람, 액티비티, 식도락이라는 4개의 테마별 인기 여행지 추천
  - 지역별 인기 여행지 추천
  - 랜덤 여행지 추천
 
<br>

### 📰 제작기간 & 팀원 소개
- 2022-09-16. ~ 2022-10-28.

<p align="center">
<img src="https://user-images.githubusercontent.com/110075438/198321462-8457a883-df7c-4b8c-9c37-ee8e1ad3c983.png">
</p>

|이름|포지션| 담당 기능 구현                                                                         |
|------|---|----------------------------------------------------------------------------------|
|서나연|Backend| 여행지 관련 기능(CRUD, 랜덤추천, 여행지찜하기, 마이페이지(찜한 여행지 목록), 관리자 기능(CRUD), HTTPS, 배포, 나만의 코스                |
|박성수|Backend| 회원관리(Spring security, JWT토큰, 소셜로그인, 권한 부여), 이메일인증(SMTP), Redis, 실시간 알림기능(SSE), 여행지 월드컵        |
|강민승|Backend| 댓글 관련 기능(CRUD, 마이페이지(댓글 목록)), 축제 관련 기능(CRUD), 다중 파일 업로드 기능, S3관리, 검색기능(Querydsl) |
|류경현|Backend||

<br>

## ⛏ BE 기술 Stack

###### Dev-Tools
- Notion
- Git
- GitHub
- Ubuntu
- PostMan

<br>

###### Back-end Stack
- Java 11
- Spring Boot 2.7.2
- Database : H2, MySQL, Redis
- Security : Spring Security, JWT, oauth2
- AWS S3, IAM, EC2, NgineX

<br>

## 🌸 아키텍쳐

![아키텍쳐](https://user-images.githubusercontent.com/97495661/198329375-c93bca10-7fb1-4d49-88b9-aab30fcef434.png)

<br>

## ⚙️ ERD

<img width="928" alt="erd" src="https://user-images.githubusercontent.com/110075438/198322563-120ff608-7df9-4e78-8ee2-158c5bcb288f.png">

## ✔ 주요 기능

- 🧳 여행지 조회
  - 한국관광공사가 제공하는 3만여건의 여행지 조회 서비스
  - 여행지의 위치(지도), 설명, 후기(작성 및 다른 이용자의 후기 확인) 조회
  - 현재 진행 또는 앞으로 예정된 축제 조회

- 🤝 여행지 선정 도움 서비스
  - 지역, 테마를 선택하여 랜덤으로 장소 추천
  - 지역, 테마를 선택하여 전체/찜한 장소 이상형 월드컵

- ⚙️관리자 기능
  - 사용자가 관리자에게 여행지 추가/수정/삭제 요청 가능
  - 요청에 대한 관리자의 답변 조회 가능(알림 및 마이페이지)

- 🔎 장소 검색 서비스
  - 돌고돌래에 등록된 장소를 검색하여 조회 가능
  - 장소의 이름을 기준으로 검색(지역 선택 가능)
  - 최근 검색어 저장

- 📋 마이페이지
  - 찜한 장소 지역별 조회
  - 찜한 장소 지도에서 조회

- 📰 내가 쓴 후기 조회
  - 내가 작성한 요청 및 관리자 답변 조회
  - 내 정보(닉네임, 비밀번호) 수정 및 회원 탈퇴

- ✍️ 여행 플래너
  - 돌고돌래에 있는 장소를 추가하여 코스 생성
  - 지도로 여행 코스 확인

- 🌸 이상형 월드컵
  - 전체 지역 32강(세부 지역, 테마) 
  - 찜한 지역 32강
<br>

### 피드백
 - 더 많은 여행지 추가
 - 풍부한 후기


