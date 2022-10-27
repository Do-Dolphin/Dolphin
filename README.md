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
|서나연|Backend| 여행지 관련 기능(CRUD, 랜덤추천, 여행지찜하기, 마이페이지(찜한 여행지 목록), HTTPS, 배포, 나만의 코스                |
|박성수|Backend| 회원관리(Spring security, JWT, 소셜로그인), 이메일인증(smtp), Redis, 알림기능(SSE), 여행지 월드컵        |
|류경현|Backend||
|강민승|Backend| 댓글 관련 기능(CRUD, 마이페이지(댓글 목록)), 축제 관련 기능(CRUD), 다중 파일 업로드 기능, S3관리, 검색기능(Querydsl) |

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

### ✔ 구현 기능

- 회원 관리
  - 회원가입, 일반로그인, 소셜로그인, JWT, 이메일 인증, Redis
- 여행지 관리
  - 한국관광공사 OPEN API
  - 여행지 찜하기, 마이페이지 찜목록 관리
- 후기 관리
  - CRUD, 마이페이지 후기 관리
- 페이지 관리
  - 메인페이지(테마별 Top10, 축제 관련 배너)
  - 상세페이지
  - 랜덤 추천 페이지
- 코스 계획하기
- 여행지 랜덤 추천
  - 전체 지역
  - 지역 선택
- 이상형 월드컵
  - 전체 지역 32강(지역별, 테마별 가능)
  - 찜한 지역 32강
- HTTPS 적용

<br>

### 피드백
 - 더 많은 여행지 추가
 - 풍부한 후기


