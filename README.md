# 📝돌고돌래

- [프론트엔드 GitHub](https://github.com/Greendeww/dolgo-dolrae)
---
### 📌 프로젝트 소개
- 여행지 추천 서비스

<br>

### 📰 제작기간 & 팀원 소개
- 2022-09-16 ~ (진행중)
 
|이름|포지션|담당 기능 구현|
|------|---|---|
|서나연|Backend|여행지 관련 기능(CRUD, 랜덤추천, 여행지찜하기, 마이페이지(찜한 여행지 목록), HTTPS, 서버 관리|
|박성수|Backend|회원가입, 로그인(Spring security, JWT, 소셜로그인), 이메일인증, Redis, 알림기능|
|류경현|Backend|여행지 정렬, 테마별 TOP10|
|강민승|Backend|댓글 관련 기능(CRUD, 마이페이지(댓글 목록)), 축제 관련 기능(CRUD), 검색기능(Querydsl)|

<br>

### ⛏ 기술스택

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

### ✔ 구현 기능

- 회원 관리
  - 회원가입, 일반로그인, 소셜로그인, JWT, 이메일 인증, Redis
- 여행지 관리
  - 한국관광공사 OPEN API
  - 여행지 랜덤 추천
  - 여행지 찜하기, 마이페이지 찜목록 관리
- 후기 관리
  - CRUD, 마이페이지 후기 관리
- 페이지 관리
  - 메인페이지(테마별 Top10, 축제 관련 배너)
  - 상세페이지
  - 랜덤 추천 페이지
- HTTPS 적용

<br>

### ❌ 미구현 기능
- 실시간 알림 기능(SSE)
- 여행지 검색 기능
- 관리자에게 요청하기 기능(여행지 등록, 수정, 삭제)
- 관리자 페이지
