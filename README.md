<p align="center">
 <img src="https://user-images.githubusercontent.com/110075438/198322820-92ee0421-79b3-479e-8a21-afaa1a917808.png">
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

|이름|포지션|담당 기능 구현|
|------|---|---|
|서나연|Backend|여행지 관련 기능(CRUD, 랜덤추천, 여행지찜하기, 마이페이지(찜한 여행지 목록), HTTPS, 서버 관리|
|박성수|Backend|회원관리(Spring security, JWT, 소셜로그인), 이메일인증, Redis, 알림기능(SSE)|
|류경현|Backend||
|강민승|Backend|댓글 관련 기능(CRUD, 마이페이지(댓글 목록)), 축제 관련 기능(CRUD), 다중 파일 업로드 기능, S3관리, 검색기능(Querydsl)|

<br>

## ⛏ BE 기술 Stack
## 프레임워크

### Spring boot

- 국내에서는 예전부터 현재까지 Back-end 언어로 JAVA가 많이 사용되고 있는 것으로 알고 있으며, Spring boot는 이전 버전들에 비해 구조가 간단하여 접근성이 좋다.

## 스택

### JWT

- Session을 이용한 방식과 JWT 인증방식 두가지를 고민하였으며, 그 결과 Session방식에 비해 별도의 저장소가 필요없는 JWT방식이 서버자원 절약에 유리하다고 판단하여 적용하였음

### OAuth2

- 사용자 입장에서는 여러 서비스들을 하나의 계정으로 관리할 수 있게되어 편해지고 개발자 입장에서는 민감한 사용자 정보를 다루지 않아 위험부담이 줄고 서비스 제공자로부터 사용자 정보를 활용할 수 있다

### redis

- 유효기간이 설정된 자료 및 잦은 조회가 예상되는 자료를 저장하는 in memory cache로 사용

### mysql

- RDB 특성상 정해진 스키마에 따라 데이터를 명확하게 구분해서 저장해야 되기 때문에 데이터 구조 설계시 불필요한 데이터 중복과 잘못된 데이터 저장 작업을 줄일 수 있어 사용

- 현재 진행중인 프로젝트 규모가 크지 않아 여러 RDB중 mysql로도 충분한 커버가 가능할 것으로 판단

### S3

- 모든 이미지 파일을 한번에 관리할수 있다

- 인증시스템을 설정하여 보안이 좋다

### JPA

- 초기 개발 과정에서 비지니스로직 구성에 집중하기 위해 팀원들의 숙련도가 비교적 높은 JPA 사용

### NginX

- SSL 인증서를 통한 HTTPS 환경 설정

- 무중단 배포 설정

### CI/CD (GitHub Actions, S3, Code Deploy)

- 빌드/ 테스트/배포를 자동화 시켜서 개발 속도 향상

- Github Actions은 별도의 server가 필요없고 설정이 간편하여 적용하기에 용이하다고 판단

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

## 🙂 아키텍쳐

![돌고돌래아키텍쳐](https://user-images.githubusercontent.com/110075438/194560203-edea3aaf-e428-466c-9ec9-89933c233875.PNG)

## 😮 ERD

<img width="928" alt="erd" src="https://user-images.githubusercontent.com/110075438/198322563-120ff608-7df9-4e78-8ee2-158c5bcb288f.png">

### ✔ 구현 기능

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
- 코스 계획하기
- 이상형 월드컵
  - 전체 지역 32강(지역별, 테마별 가능)
  - 찜한 지역 32강
- HTTPS 적용

<br>

### 피드백
 - 더 많은 여행지 추가
 - 풍부한 후기


