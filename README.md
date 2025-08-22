# 싸이월드

추억이 담긴 싸이월드의 향수를 다시 불러 일으키고 싶어 시작한 프로젝트 입니다.

<br>

---

### 기술 스택 (Tech Stack)

#### Backend
<p>
    <img alt="Java" src="https://img.shields.io/badge/java-B3CCE8?style=flat-square&logo=OpenJDK&logoColor=white">
    <img alt="Spring Boot" src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=Spring&logoColor=white"/>
    <img alt="Spring Security" src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square&logo=Spring&logoColor=white"/>
    <img alt="JPA" src="https://img.shields.io/badge/JPA-A8D8EA?style=flat-square&logo=Hibernate&logoColor=white"/>
    <img alt="MySQL" src="https://img.shields.io/badge/MySQL-F29111?style=flat-square&logo=MySQL&logoColor=white">
</p>

#### Frontend
<p>
    <img alt="HTML5" src="https://img.shields.io/badge/HTML5-FFB3BA?style=flat-square&logo=HTML5&logoColor=white">
    <img alt="CSS3" src="https://img.shields.io/badge/CSS3-BAFFC9?style=flat-square&logo=CSS3&logoColor=white">
    <img alt="JavaScript" src="https://img.shields.io/badge/JavaScript-FFFFBA?style=flat-square&logo=JavaScript&logoColor=black">
</p>


#### Deployment & Infra
<p>
    <img alt="AWS" src="https://img.shields.io/badge/AWS-FFD47F?style=flat-square&logo=amazonaws&logoColor=white">
    <img alt="Docker" src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white"/>
</p>

<br>

---

### 테이블 설계 (Database Schema)

<details>
  <summary>ERD 보기 (Click to expand)</summary>
  <img alt="ERD" src="back/erd/cyworld_erd_20250719_v01.png" />
</details>

<br>

---

### 시스템 아키텍처 (System Architecture)
- 업데이트 예정

<br>

---

### 주요 기능 (Features) 25.08.09

![progress](https://img.shields.io/badge/Progress-50%25-blue)

- [x] 회원 관리: 회원가입, JWT 기반 로그인/로그아웃, Spring Security를 이용한 인증/인가
- [x] 미니홈피: 개인화된 프로필, 오늘의 감정, 방문자 수(Today/Total) 자동 집계 및 스케줄링을 통한 초기화
- [x] 일촌 관리: 일촌 신청/수락/거절/취소, 관계 끊기, 받은/보낸 신청 목록 조회, 일촌명 등 쌍방 관계 관리
- [x] 게시판 (방명록/다이어리/일촌평): 타입별 게시글 CRUD, 페이징 및 주인장/일촌별 상세 권한 관리
- [ ] 사진첩: 사진 업로드 및 앨범 관리 기능
- [ ] BGM: 미니홈피 배경음악 설정 및 재생 기능 구현
- [ ] 프론트엔드 연동: 개발된 백엔드 API와 프론트엔드 서버를 연동하여 실제 서비스 화면 구현
- [ ] 배포: AWS EC2, RDS 등 클라우드 환경에 프로젝트를 배포하여 실제 서비스 운영
