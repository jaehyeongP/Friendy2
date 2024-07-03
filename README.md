# Friendy Backend Server

> **KOSTA 272기 최종 프로젝트** <br/> **개발기간: 2024.05.29   ~ 2024.06.28** <br/> 팀원 : 5명

---
## 프로젝트 소개

- 개인 및 온라인 커뮤니티에서 취미가 비슷한 사람들이 직접 소모임을 만들 수 있는 서비스. 
- 소모임 게시판에서 모임을 만들고, 유저들이 해당 모임에 신청을 해서 수락된 사람들끼리 채팅을 할수있음.
- 사진 게시판, 익명게시판, 자유 게시판 등의 다양한 게시판 기능.
- 웹 프로젝트의 백엔드 서버

👉 [Frontend Server](https://github.com/jaehyeongP/friendy_front)

### Environment

![intellij](https://img.shields.io/badge/intellij-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=Visual%20Studio%20Code&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)

### Back-end

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=Java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0.RELEASE-green?style=for-the-badge&logo=Spring&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.3.0.RELEASE-green?style=for-the-badge&logo=Spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-3.3.0.RELEASE-green?style=for-the-badge&logo=Spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-5.0-green?style=for-the-badge&logo=Java&logoColor=white)
![Socket.io](https://img.shields.io/badge/Socket.io-black?style=for-the-badge&logo=socket.io&badgeColor=010101)

### DataBase
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=oracle&logoColor=white)


### Api
[![Kakao Pay API](https://img.shields.io/badge/Kakao%20Pay%20API-FFCD00?style=for-the-badge&logo=kakao&logoColor=black)](https://developers.kakao.com/docs/latest/ko/kakaopay)
[![CoolSMS API](https://img.shields.io/badge/CoolSMS%20API-5B9BD5?style=for-the-badge&logo=coolpad&logoColor=white)](https://www.coolsms.co.kr/)
![Gmail](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white)
![KakaoChatBot](https://img.shields.io/badge/kakao_ChatBot-ffcd00.svg?style=for-the-badge&logo=kakaoChatBot&logoColor=000000)
### Communication

![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white)

---


## 주요 기능 🎁

### 🛒 소셜 회원가입 및 Jwt 사용
- 소셜 로그인 시 API를 통해 받은 정보를 사용하여 JWT를 구현
- 소셜 로그인의 API의 인증 토큰을 사용하지 않으므로 보안부담 감소

### 🛒 기프티콘 등록 및 사용
- 카카오 챗봇, 파일로 등록시 Naver Cloud Ocr을 사용하여 읽은 값을 DB의 Product 테이블을 통해 검증
- 기프티콘 등록 시 DB의 Product 테이블에 존재하는 상품이면 바로 기프티콘 등록, 존재하지 않는 상품이면 관리자의 검수를 통해 등록 처리
- 기프티콘으로 등록되면 입력했던 사진은 보안상 이유로 서버에서 삭제
- 사용 시 기프티콘 바코드 이미지를 생성하고 SMS를 통해 전송

### 🛒 기프티콘 거래 및 포인트 충전
- 등록 처리된 기프티콘은 판매 가능
- 카카오 페이를 통해 포인트 충전, 충전된 포인트로 기프티콘을 구매

### 관리자
- Product DB을 초기화 기프티콘 API excel 파일을 통해 Product DB에 상품을 등록
- 검수 상태의 기프티콘을 확인하여 검수 완료 및 거절

---
