![foodie-log-상세이미지](https://github.com/FoodieLog/foodie-log-server/assets/65496092/89f52bbb-6ddc-4773-b4de-ba16ca61c620)

<div align=center><h1> 🍽️ FOODIE LOG (푸디로그 - 나만의 작은 맛집 지도) </h1></div>

<div align=center><a href="https://www.foodielog.shop"><img src="https://github.com/FoodieLog/foodie-log-server/assets/65496092/15aafdd5-9000-4940-a69f-2c4258b179d1"></a></div>
<div align=center>🔼 QR 코드 스캔해서 다운받기 </div>

> **프로젝트 기간** : 2023.7.5. ~ 2023.10.10 <br>
> **배포 주소** : [푸디로그](https://www.foodielog.shop)<br>
> **백엔드 Repository** : [백엔드](https://github.com/FoodieLog/foodie-log-server) <br>
> **프론트 Repository** : [프론트엔드](https://github.com/FoodieLog/foodie-log-client) <br>





---

## 🛠️ 기술 스택

<p align=center>
  <img src="https://img.shields.io/badge/Java (JDK 11)-437291?style=flat&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Boot (2.7.14)-6DB33F?style=flat&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=flat&logo=jsonwebtokens&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL (8.0.34)-4479A1?style=flat&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white">
</p>

<p align=center>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/Github Actions-2088FF?style=flat&logo=github actions&logoColor=white">
  <img src="https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=jenkins&logoColor=white">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/Amazon S3-569A31?style=flat&logo=amazons3&logoColor=white">
  <img src="https://img.shields.io/badge/Amazon Route 53-8C4FFF?style=flat&logo=amazonroute53&logoColor=white">
</p>

<p align=center>
  <img src="https://img.shields.io/badge/github-181717?style=flat&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=flat&logo=IntelliJ IDEA&logoColor=white">
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=flat-flat&logo=figma&logoColor=white">
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white">
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=white">
  <img src="https://img.shields.io/badge/notion-000000?style=flat&logo=notion&logoColor=white">
  <img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=discord&logoColor=white">
</p>

## 🏛️ 아키텍쳐

![foodie-log-아키텍쳐](https://github.com/FoodieLog/foodie-log-server/assets/65496092/510a661d-184f-47e8-88f6-88975075e969)

## 📦 ERD

![foodie-log-ERD](https://github.com/FoodieLog/foodie-log-server/assets/65496092/855d282c-c7c8-4908-989f-d0fab7c98348)

## 📄 API 명세

서버 배포 전: Postman mock server를 구축하여 프론트엔드-백엔드 개발을 병렬적으로 진행

![foodie-log-API(1)](https://github.com/FoodieLog/foodie-log-server/assets/65496092/f2e13c82-3d1a-4a90-9e61-5fc1498bd616)

서버 배포 후: Swagger 페이지로 문서화 하여 프론트엔드 공유

![foodie-log-API(2)](https://github.com/FoodieLog/foodie-log-server/assets/65496092/e56fbdee-652c-4975-93bf-3a3606f22eff)

## 🧑‍💻 백엔드 개발팀

| ![](https://avatars.githubusercontent.com/u/65496092?v=4)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | ![](https://avatars.githubusercontent.com/u/84082544?v=4) | ![](https://avatars.githubusercontent.com/u/86757234?v=4) |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------|:----------------------------------------------------------|
| [엄채원](https://github.com/chaewon12)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | [손영준](https://github.com/sohn919)                         | [황인영](https://github.com/inyoung0215)                     |
| - PL(Project Leader)<br/>- Project 초기 setting<br/>- 인증/인가 구현<br/>&nbsp;&nbsp;&nbsp;&nbsp;- Spring Security 설정<br/>&nbsp;&nbsp;&nbsp;&nbsp;- JWT 토큰 기반 인증 구현<br/>&nbsp;&nbsp;&nbsp;&nbsp;- 자체 회원가입(이메일 인증) 구현<br/>&nbsp;&nbsp;&nbsp;&nbsp;- 카카오 로그인 연동<br/>- 회원 API 구현<br/>&nbsp;&nbsp;&nbsp;&nbsp;- 알림 설정<br/>&nbsp;&nbsp;&nbsp;&nbsp;- 뱃지 신청<br/>&nbsp;&nbsp;&nbsp;&nbsp;- 프로필, 비밀번호 수정<br/>&nbsp;&nbsp;&nbsp;&nbsp;- 로그아웃, 탈퇴<br/>&nbsp;&nbsp;&nbsp;&nbsp;- 회원 검색<br/>- 관리자 서버 신고 관리 API 구현<br/>- EC2 + Docker 서버 환경 구축<br/>- CI/CD 파이프라인 구축<br/>&nbsp;&nbsp;&nbsp;&nbsp;- GitHub Actions 를 이용한 CI 테스트 자동화<br/>&nbsp;&nbsp;&nbsp;&nbsp;- Jenkins 를 이용한 배포 자동화 | - 내용1<br/> - 내용2                                          | - 내용1<br/> - 내용2                                          |



