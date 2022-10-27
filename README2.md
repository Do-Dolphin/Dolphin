![배너용](이미지주소)

<div align="center"><h3>돌고돌래, 여행을 어디가야할지 궁금할때! 랜덤 여행지 추천부터 사용자가 원하는 곳을 정할 수 있도록 수많은 여행지를 추천해주는 서비스입니다. </h3></div>

## 🤩 돌고돌래 [서비스 링크 바로가기](http://dolgo.site/)
## 😖 돌고돌래 [발표 영상 바로가기]()
## 🤗 돌고돌래 [시연 영상 바로가기]()

## 😆 프로젝트 Git address

- Back-end Github    https://github.com/Do-Dolphin/dolphin
- Front-end Github   

## 😶 돌고돌래 팀원 소개
<!-- 표 시작 -->
<div align="center">
<table>
      <thead>
        <tr>
          <th>역할</th><th>이름</th><th>개인 Git 주소</th><th>개인 메일 주소</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>강민승님</td><td>https://github.com/g4dalcom</td><td>refromto@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>( LV ) 서나연님</td><td></td><td>manager.kim86@gmail.com</td>
        </tr>
        <tr>
           <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>박성수님</td><td>https://github.com/prscsl</td><td>0527wj@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"></td><td>류경현님</td><td></td><td>junghunwook456@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"></td><td>최도규님</td><td>https://github.com/ermael35</td><td>264826@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"></td><td>이윤재님</td><td>https://github.com/Greendeww</td><td>gksrufdla@naver.com</td>
        </tr>
        <tr>
          <td><img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"></td><td>( L ) 김소연님</td><td>https://github.com/sooo0y</td><td>hjy583@naver.com</td>
        </tr>
      </tbody>
    </table>
</div>
<!--표 끝--> 


## 😤 BE 기술 Stack

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

### EC2

- 

### NginX

- SSL 인증서를 통한 HTTPS 환경 설정

- 무중단 배포 설정

### CI/CD (GitHub Actions, S3, Code Deploy)

- 빌드/ 테스트/배포를 자동화 시켜서 개발 속도 향상

- Github Actions은 별도의 server가 필요없고 설정이 간편하여 적용하기에 용이하다고 판단


## 🙂 아키텍쳐

![돌고돌래아키텍쳐](https://user-images.githubusercontent.com/110075438/194560203-edea3aaf-e428-466c-9ec9-89933c233875.PNG)

## 😮 Data base 설계

![돌고돌래ERD](https://user-images.githubusercontent.com/110075438/194560469-5808c58a-73a1-40a0-9d45-cf054450eaa3.PNG)

## 🤩 돌고돌래 주요 기능

