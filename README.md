# 🎵 AIGroove - 관리자 웹 페이지

> AI 기반 음악 리듬 게임 플랫폼 **AIGroove**의 관리자 웹 페이지입니다.
> 사용자 관리, 콘텐츠 관리, AI 모델 운영까지 플랫폼 전반을 관리하는 백오피스 시스템을 설계하고 구현했습니다.

<br>

## 📌 프로젝트 개요

| 항목 | 내용 |
|---|---|
| **프로젝트명** | AIGroove - AI 기반 음악 리듬 게임 |
| **개발 기간** | 2025.03 ~ 2025.06 (4개월) |
| **팀 구성** | 4인 (가천대학교 종합프로젝트) |
| **담당 역할** | 관리자 웹 페이지 전체 (프론트엔드 + 백엔드) |
| **GitHub** | [Backend (Spring Boot)](https://github.com/kimhyeongjun-1204/aigroove) · [Admin Frontend (React)](https://github.com/kimhyeongjun-1204/aigroove-admin-front) |

<br>

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT_(jjwt_0.11.5)-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger_(SpringDoc_2.0.2)-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge&logo=lombok&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

### Frontend
![React](https://img.shields.io/badge/React_18.2-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![React Router](https://img.shields.io/badge/React_Router_6-CA4245?style=for-the-badge&logo=reactrouter&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)

<br>

## 🖥 주요 기능 및 화면

### 1. 로그인

JWT 기반 관리자 인증 시스템입니다. BCrypt를 이용한 비밀번호 암호화와 토큰 기반 인증으로 보안을 강화했습니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/login.jpg" width="80%"/>
</p>

- Spring Security + JWT 기반 인증/인가
- BCrypt 비밀번호 암호화
- 토큰 만료 시간: 24시간

---

### 2. 대시보드

로그인 후 메인 화면으로, 서비스 핵심 지표와 시스템 로그를 한눈에 확인할 수 있습니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/dashboard.jpg" width="80%"/>
</p>

- 총 회원 수, 일일 접속자 수, 총 게시글 수 실시간 표시
- AI 학습 커버 소요 시간 지표
- 시스템 로그 최근 이력 조회 (관리자, 시간, 이벤트)

---

### 3. 문의사항 관리

사용자가 앱에서 보낸 문의를 접수하고 답변을 작성하면, Spring Mail을 통해 이메일 알림까지 자동 발송됩니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/inquiry-list.jpg" width="80%"/>
</p>
<p align="center">
  <img src="./aigroove-admin-screenshots/inquiry-answer.jpg" width="80%"/>
</p>
<p align="center">
  <img src="./aigroove-admin-screenshots/email-notification.jpg" width="80%"/>
</p>

- 전체 문의 목록 조회 (제목, 작성자, 작성일, 답변 상태)
- 제목/작성자 검색 및 답변 대기 필터링
- 페이지네이션 (6페이지 이상 데이터 처리)
- 답변 작성 → Gmail SMTP를 통한 이메일 알림 자동 발송

---

### 4. 공지사항 관리

공지사항 CRUD 기능을 구현했습니다. 관리자 웹에서 작성된 공지가 사용자 앱에 실시간 반영됩니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/notice-list.jpg" width="80%"/>
</p>

- 공지사항 작성, 수정, 삭제 (31개 이상의 공지 관리)
- 작성자, 작성시간 기록
- 페이지네이션 (7페이지)

---

### 5. 관리자 관리 (승인 시스템)

새로운 관리자 가입 요청을 기존 관리자가 승인/거절하는 프로세스를 구현했습니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/admin-management.jpg" width="80%"/>
</p>

- 관리자 목록 조회 (11명) - 가입일, 이름, 아이디, 생년월일, 역할
- 승인 대기 목록 (5명) - 승인/거절 버튼으로 권한 관리
- 관리자 탈퇴 처리

---

### 6. AI 데이터셋 관리

AI 모델 학습에 사용되는 데이터셋을 업로드, 다운로드, 삭제할 수 있습니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/ai-dataset.jpg" width="80%"/>
</p>

- 데이터셋 목록 조회 (FLAC_MID_100/200/500, Slakh2100, WAV_MID_100 등)
- 파일 크기 표시 (1.3GB ~ 102.41GB)
- 데이터셋 업로드, 다운로드, 삭제 기능

---

### 7. AI 학습 실행

데이터셋을 선택하고 하이퍼파라미터를 설정하여 AI 모델 학습을 실행합니다. 학습 진행률을 실시간으로 모니터링할 수 있습니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/ai-training.jpg" width="80%"/>
</p>
<p align="center">
  <img src="./aigroove-admin-screenshots/ai-training-progress.jpg" width="80%"/>
</p>

- 학습 데이터셋 선택 (체크박스)
- 하이퍼파라미터 설정: learning_rate, batch_size, epoch_number, input_size, hidden_size, num_layers
- 학습 진행률 실시간 표시 (프로그래스 바)
- 학습 시작/취소 제어
- 학습 완료 시 버전 정보 입력 모달

---

### 8. AI 모델 버전 관리

학습이 완료된 AI 모델의 버전별 성능 지표(Accuracy, F1-Score)를 시각적으로 관리합니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/version-management.jpg" width="80%"/>
</p>

- 버전 목록 (v1.1 ~ v3.1) - 출시일, Accuracy, F1-Score 표시
- 성능 지표 바 그래프 시각화
- 활성 버전 배지 표시 (사용중)
- 새 버전 추가 및 활성 버전 전환

---

### 9. 서버 상태 확인

서버의 실시간 리소스 사용량을 모니터링합니다.

<p align="center">
  <img src="./aigroove-admin-screenshots/server-status.jpg" width="80%"/>
</p>

- CPU, 메모리, 디스크 사용률 프로그래스 바
- 서버 가동 시간 표시

<br>

## 📂 프로젝트 구조

### Backend (aigroove)
```
src/main/java/com/game4men/aigroove/
├── AigrooveApplication.java            # Spring Boot 메인 클래스
│
├── admin/                              # 관리자 모듈 ⭐ 담당
│   ├── controller/
│   │   ├── AdminController.java           # 관리자 관리 API
│   │   ├── AdminUserController.java       # 회원 관리 API
│   │   ├── InquiryController.java         # 문의사항 API
│   │   ├── LoginController.java           # 관리자 로그인 API
│   │   └── NoticeController.java          # 공지사항 API
│   ├── DTO/
│   │   ├── AdminListResponse.java
│   │   ├── AdminResponse.java
│   │   ├── InquiryResponse.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── SignupRequest.java
│   │   ├── SignupResponse.java
│   │   └── UserResponse.java
│   └── service/
│       ├── AdminSvc.java                  # 관리자 CRUD 로직
│       ├── AdminUserSvc.java              # 회원 관리 로직
│       ├── EmailService.java              # 이메일 발송 (Gmail SMTP)
│       ├── InquirySvc.java                # 문의사항 처리 로직
│       ├── LoginSvc.java                  # JWT 인증 로직
│       └── NoticeSvc.java                 # 공지사항 CRUD 로직
│
├── game/                               # 게임 모듈
│   ├── controller/
│   │   ├── BadgeController.java
│   │   ├── MiscController.java
│   │   ├── MultiplayController.java
│   │   ├── RankingController.java
│   │   ├── SingleplayController.java
│   │   ├── SongController.java
│   │   └── UserController.java
│   ├── DTO/
│   │   ├── LoginRequest.java, JwtResponse.java
│   │   ├── GameRoomDTO.java, PlayStatusDTO.java, PlayResultDTO.java
│   │   ├── UserDTO.java, BadgeDTO.java, RankingDTO.java
│   │   ├── NoticeDTO.java, InquiryDTO.java
│   │   ├── BaseResponse.java, SuccessResponse.java, ErrorResponse.java
│   │   └── MapFile.java, ImageGenerationResponse.java
│   ├── exception/
│   │   ├── DownloadIncompleteException.java
│   │   └── GameAlreadyStartedException.java
│   └── service/
│       ├── AudioConvertSvc.java
│       ├── BadgeSvc.java
│       ├── MiscSvc.java
│       ├── MultiplaySvc.java
│       ├── ThumbnailSvc.java
│       └── UserSvc.java
│
└── common/                             # 공통 모듈
    ├── config/
    │   ├── SecurityConfig.java            # Spring Security 설정
    │   ├── SwaggerConfig.java             # Swagger API 문서 설정
    │   └── WebConfig.java                 # CORS 설정
    ├── entity/
    │   ├── Admin.java                     # 관리자
    │   ├── User.java                      # 사용자
    │   ├── Inquiry.java                   # 문의사항
    │   ├── Notice.java                    # 공지사항
    │   ├── GameRoom.java                  # 게임 방
    │   ├── GameStatus.java                # 게임 상태
    │   ├── Badge.java                     # 뱃지
    │   ├── Ranking.java                   # 랭킹
    │   ├── DatasetInfo.java               # AI 데이터셋 정보
    │   ├── ModelInfo.java                 # AI 모델 정보
    │   ├── Log.java                       # 로그
    │   └── DailyLog.java                  # 일일 로그
    ├── repository/
    │   ├── AdminRepository.java
    │   ├── UserRepository.java
    │   ├── InquiryRepository.java
    │   ├── NoticeRepository.java
    │   ├── GameRoomRepository.java
    │   ├── GameStatusRepository.java
    │   ├── BadgeRepository.java
    │   ├── DatasetInfoRepository.java
    │   ├── ModelInfoRepository.java
    │   ├── DailyLogRepository.java
    │   └── LoginRepository.java
    └── utils/
        ├── JwtUtils.java                  # JWT 토큰 생성/검증
        └── JwtAuthenticationFilter.java   # JWT 인증 필터
```

### Frontend (aigroove-admin-front)
```
src/
├── App.js                              # 메인 앱 & 라우팅 설정
├── index.js                            # 엔트리 포인트
│
├── components/
│   ├── js/
│   │   ├── Login.js                       # 로그인 페이지
│   │   ├── Signup.js                      # 회원가입 페이지
│   │   ├── SignupSuccess.js               # 회원가입 완료
│   │   ├── Intro.js                       # 인트로 페이지
│   │   ├── IntroHeader.js                 # 인트로 헤더
│   │   ├── Dashboard.js                   # 대시보드
│   │   ├── UserManagement.js              # 사용자 관리
│   │   ├── UserDetailModal.js             # 사용자 상세 모달
│   │   ├── InquiryManagement.js           # 문의 목록 관리
│   │   ├── InquiryDetail.js               # 문의 상세 보기
│   │   ├── InquiryAnswer.js               # 문의 답변 작성
│   │   ├── NoticeManagement.js            # 공지사항 관리
│   │   ├── NoticeWrite.js                 # 공지사항 작성
│   │   ├── NoticeEdit.js                  # 공지사항 수정
│   │   ├── AdminManagement.js             # 관리자 관리
│   │   ├── DefaultSongTable.js            # 기본 곡 목록
│   │   ├── DatasetList.js                 # AI 데이터셋 목록
│   │   ├── DatasetAdd.js                  # AI 데이터셋 추가
│   │   ├── AITrainManager.js              # AI 학습 실행 관리
│   │   ├── VersionManagement.js           # AI 모델 버전 관리
│   │   ├── ModelDetailModal.js            # 모델 상세 모달
│   │   ├── ServerStatus.js                # 서버 상태 확인
│   │   ├── LogViewer.js                   # 로그 조회
│   │   └── side/
│   │       ├── Sidebar.js                    # 사이드바 네비게이션
│   │       ├── TopHeader.js                  # 상단 헤더
│   │       ├── Pagination.js                 # 페이지네이션
│   │       ├── SearchBox.js                  # 검색 박스
│   │       ├── ConfirmPopup.js               # 확인 팝업
│   │       ├── ErrorPopup.js                 # 에러 팝업
│   │       └── ProtectedRoute.js             # 인증 라우트 가드
│   └── css/
│       ├── Login.css, Signup.css, Dashboard.css ...
│       ├── img/                           # 이미지 리소스
│       └── side/                          # 공통 컴포넌트 스타일
│
├── config/
│   └── config.js                       # API 서버 주소 등 설정
├── services/
│   ├── api.js                          # Axios API 클라이언트
│   └── authService.js                  # 인증 서비스 (JWT 관리)
└── types/                              # 타입 정의
```

<br>

## 📡 API 명세

### 인증
| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/admin/login` | 관리자 로그인 (JWT 발급) |
| POST | `/admin/signup` | 관리자 회원가입 요청 |

### 대시보드
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/admin/dashboard` | 대시보드 구성 정보 요청 |

### 사용자 관리
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/admin/user` | 사용자 정보 리스트 요청 |
| GET | `/admin/user/search?keyword={keyword}` | 해당 검색어를 포함하는 사용자 정보 리스트 요청 |
| GET | `/admin/user/detail?id={user_id}` | 사용자 상세 정보 요청 |
| DELETE | `/admin/user_account?id={user_id}` | 사용자 계정 삭제 요청 |

### 문의사항 관리
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/admin/inquiry` | 사용자 문의글 리스트 요청 |
| GET | `/admin/inquiry/check` | 답변 안된 문의글 리스트 요청 |
| GET | `/admin/inquiry/search?keyword={keyword}` | 해당 검색어를 포함하는 문의글 리스트 요청 |
| POST | `/admin/inquiry?id={inquiry_id}` | 사용자 문의 답변 추가 요청 |

### 공지사항 관리
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/admin/notice` | 공지사항 리스트 요청 |
| POST | `/admin/notice` | 공지사항 추가 요청 |
| PATCH | `/admin/notice` | 공지사항 수정 요청 |
| DELETE | `/admin/notice?id={notice_id}` | 공지사항 삭제 요청 |

### 관리자 관리
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/admin/admin` | 관리자 정보 리스트 요청 |
| POST | `/admin/admin/accept?id={admin_id}` | 관리자 승인 요청 |
| DELETE | `/admin/admin?id={admin_id}` | 관리자 삭제 요청 |

### 콘텐츠 & 시스템
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/admin/song` | 기본 제공 곡 정보 리스트 요청 |
| GET | `/admin/server_status` | 서버 정보 요청 |
| GET | `/admin/logs` | 로그 정보 리스트 요청 |
| GET | `/admin/logs/search?keyword={keyword}` | 해당 검색어를 포함하는 로그 리스트 요청 |
| GET | `/admin/logs/filter?level={level}` | 특정 로그 레벨로 필터링된 로그 요청 |

> 전체 API 문서는 서버 실행 후 Swagger UI에서 확인할 수 있습니다: `http://localhost:8080/swagger-ui/index.html`

<br>

## 🏗 아키텍처

```
┌─────────────────────┐       ┌──────────────────────────┐       ┌───────────┐
│  Admin Frontend     │       │  Backend Server           │       │  MariaDB  │
│  (React 18)         │──────▶│  (Spring Boot 3.4.4)      │──────▶│           │
│  React Router DOM   │  API  │                           │  JPA  │  aigroove │
│  Axios              │◀──────│  ┌─ admin/ ⭐ 담당         │◀──────│           │
└─────────────────────┘       │  │  - 관리자 인증 (JWT)    │       └───────────┘
                              │  │  - 사용자/문의/공지 관리 │
                              │  │  - AI 모델 운영         │
                              │  ├─ game/                  │
                              │  │  - 싱글/멀티플레이       │
                              │  │  - 랭킹/뱃지            │
                              │  └─ common/                │
                              │     - Security + JWT 필터  │
                              │     - Entity / Repository  │
                              └────────────┬───────────────┘
                                           │
                              ┌────────────▼───────────────┐
                              │      Spring Mail           │
                              │    (Gmail SMTP 알림)       │
                              └────────────────────────────┘
```

<br>

## 💡 핵심 구현 사항

### JWT 기반 인증 시스템
Spring Security와 JWT를 결합하여 Stateless 인증을 구현했습니다. `JwtAuthenticationFilter`에서 요청 헤더의 토큰을 검증하고, `SecurityConfig`에서 관리자 API 경로에 대한 인가 처리를 설정했습니다.

### 관리자 승인 프로세스
신규 관리자가 회원가입을 요청하면 "승인 대기" 상태로 등록됩니다. 기존 관리자가 승인 처리를 해야만 로그인이 가능하도록 설계하여, 무분별한 관리자 계정 생성을 방지했습니다.

### 이메일 알림 시스템
사용자 문의에 답변을 작성하면 Spring Mail을 통해 Gmail SMTP로 이메일 알림이 자동 발송됩니다. 비동기 처리로 답변 저장과 이메일 발송이 독립적으로 동작합니다.

### AI 모델 운영 관리
데이터셋 업로드부터 하이퍼파라미터 설정, 학습 실행, 진행률 모니터링, 버전 관리까지 AI 모델의 전체 라이프사이클을 관리자 웹에서 제어할 수 있습니다.

<br>

## 🔧 트러블 슈팅

### 1. AI 학습 진행률 조회 시 404 에러

**문제**: AI 학습 실행(POST) 직후 학습 진행률 조회(GET)를 요청하면 `_status.json` 파일이 없다는 404 에러가 발생했습니다.

**원인**: 학습 실행 API는 Spring Boot에서 Python 스크립트(`main.py`)를 비동기로 실행하는 구조였습니다. Python 스크립트가 `_status.json` 파일을 생성하기 전에 프론트엔드에서 진행률 조회 요청을 보내고 있었습니다. POST 요청과 파일 생성이 단일 프로세스가 아니기 때문에 발생한 **비동기 타이밍 이슈**였습니다.

**해결**: AI 담당 팀원과 함께 Python 스크립트의 `_status.json` 생성 시점을 분석한 후, 프론트엔드에서 학습 실행 POST 요청 후 1초의 딜레이를 두고 진행률 polling을 시작하도록 수정했습니다.
```javascript
// AITrainManager.js
await axios.post('/admin/ai/train', trainConfig);
setTimeout(() => {
  startPollingStatus();  // 1초 후 진행률 polling 시작
}, 1000);
```

---

### 2. Spring Security + React SPA 통합 배포 시 인증 충돌

**문제**: React 프론트엔드를 Spring Boot에 통합하여 학과 서버(Linux)에 배포했을 때, 로컬에서는 정상 동작하지만 서버에서는 프론트엔드 페이지 접속 시 오류가 발생했습니다. 새로고침하면 페이지가 랜덤으로 작동하거나 접속 자체가 불가능했고, 서버 로그에는 `AnonymousAuthenticationFilter` 관련 오류가 반복적으로 출력되었습니다.

**원인**: 원인이 복합적이었습니다.
1. **Spring Security 인증 충돌**: React 빌드 파일을 Spring Boot에 통합 서빙하면서, `/admin/dashboard`, `/admin/notice` 같은 React 라우팅 경로를 Spring Security가 API 엔드포인트로 인식하여 인증을 요구했습니다. 로컬 개발 환경에서는 React(3000포트)와 Spring Boot(8080포트)가 분리되어 있어 이 문제가 발생하지 않았습니다.
2. **SPA 라우팅 미처리**: 새로고침 시 브라우저가 서버에 직접 URL을 요청하는데, Spring Boot에 해당 경로의 실제 리소스가 없어 404가 발생했습니다.
3. **포트 점유 문제**: 기존 버전(v0.4)이 같은 포트(60002)를 점유하고 있어 새 버전이 정상 기동되지 않는 경우도 있었습니다.

**해결**: 팀원과 함께 포트 점유 확인(Process kill), Linux 파일 권한 확인 등 환경 문제를 먼저 제거한 후, 다음 2가지를 수정했습니다.

`SecurityConfig.java`에서 React 정적 리소스와 인증 불필요 경로를 허용 처리:
```java
.requestMatchers("/", "/index.html", "/static/**", "/favicon.ico").permitAll()
.requestMatchers("/admin/login", "/admin/signup").permitAll()
```

`WebConfig.java`에서 SPA 라우팅을 위해 알 수 없는 경로를 `index.html`로 포워딩하도록 설정을 추가했습니다.

---

### 3. 배포 환경에서 API 요청 실패 (Base URL 불일치)

**문제**: 학과 서버(`ceprj.gachon.ac.kr:60002`)에 배포 후 회원가입 등 API 호출 시 "서버와의 통신 중 오류가 발생했습니다" 에러가 발생하고, 브라우저 콘솔에 `ERR_CONNECTION_REFUSED` 에러가 다수 출력되었습니다.

**원인**: React의 API 설정 파일(`config.js`)에 Base URL이 `http://localhost:60002`로 하드코딩되어 있었습니다. 로컬에서는 정상이지만, 학과 서버에 배포하면 사용자의 브라우저가 자신의 PC에서 60002 포트를 찾게 되어 연결이 거부되었습니다.

**해결**: API Base URL을 배포 환경에 맞게 수정했습니다.
```javascript
// config.js
// 변경 전
const API_BASE_URL = "http://localhost:60002";

// 변경 후
const API_BASE_URL = "http://ceprj.gachon.ac.kr:60002";
```
이 경험을 통해 개발/배포 환경별로 설정을 분리하는 것의 중요성을 배웠으며, 환경 변수(`.env`)를 활용한 설정 관리 방식을 학습했습니다.

---

### 4. JPA 쿼리 중복 결과 오류 (NonUniqueResultException)

**문제**: 운영 중 서버 로그에 `Query did not return a unique result: 2 results were returned` 에러가 발생했습니다.

**원인**: JPA Repository에서 단일 결과를 기대하는 쿼리 메서드를 사용했는데, DB에 동일 조건의 데이터가 2건 이상 존재하여 `NonUniqueResultException`이 발생한 것이었습니다.

**해결**: Repository 쿼리 메서드의 반환 타입을 단건(`Optional<Entity>`)에서 리스트(`List<Entity>`)로 변경하고, DB의 중복 데이터를 정리한 뒤 유니크 제약 조건을 추가하여 데이터 정합성을 보장했습니다.

<br>

## 👥 팀 구성

| 이름 | 역할 |
|---|---|
| **김형준** | 관리자 웹 페이지 (프론트엔드 + 백엔드) |
| 반재혁 | 게임 클라이언트 (Unreal Engine) |
| 김동현 | 게임 서버 / 멀티플레이 |
| 문유신 | AI 모델 (BiLSTM 기반 음악 분석) |

<br>

## ⚙️ 실행 방법

### Backend
```bash
# 1. 클론
git clone https://github.com/kimhyeongjun-1204/aigroove.git
cd aigroove

# 2. application.properties 에서 DB 설정 수정
# spring.datasource.url=jdbc:mariadb://localhost:3306/aigroove
# spring.datasource.username=[DB_USERNAME]
# spring.datasource.password=[DB_PASSWORD]

# 3. 빌드 및 실행
./gradlew build
./gradlew bootRun
```

### Frontend
```bash
# 1. 클론
git clone https://github.com/kimhyeongjun-1204/aigroove-admin-front.git
cd aigroove-admin-front

# 2. 의존성 설치
npm install

# 3. 개발 서버 실행
npm start
```

### 접속
| 서비스 | URL |
|---|---|
| 관리자 웹 | `http://localhost:3000` |
| Backend API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
