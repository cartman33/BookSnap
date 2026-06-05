# 📖 Software Requirements Specification (SRS): BookSnap

## 1. Project Overview
- **Project Name:** BookSnap (나만의 문장 수집 다이어리)
- **Goal:** 독서 중 기록하고 싶은 페이지를 촬영하고, OCR(광학 문자 인식)을 통해 원하는 문장을 추출하여 날짜별, 도서별로 아카이빙하는 모바일 독서 아카이빙 서비스.
- **Target Audience:** 책의 문장을 타이핑하기 귀찮아 사진으로 남기지만 정작 다시 찾아보지 않는 독서가. (최초 테스트 타겟: 개발자의 지인 및 소규모 그룹)
- **Key Value:** 사용자가 타이핑하는 수고로움 없이, 책의 페이지 사진 촬영과 드래그(선택)만으로 원하는 문장을 쉽게 텍스트 데이터로 변환하고 보관할 수 있도록 돕는다.

---

## 2. Tech Stack & Infrastructure

### 2.1 Application Stack
| 분류 | 기술 스택 | 주요 용도 및 라이브러리 |
| :--- | :--- | :--- |
| **IDE** | Cursor | AI 기반 풀스택 코드 자동 생성 및 리팩토링 |
| **Frontend** | React Native (Expo) | `expo-camera`, `react-native-calendars` |
| **Backend** | Java (Spring Boot 3.x) | RESTful API 서버, Spring Security, JWT, JPA |
| **Database** | MySQL 8.0 | 관계형 데이터베이스 (사용자, 도서, 기록 간의 연관 관계 매핑) |

### 2.2 External APIs & Services
| 분류 | 서비스 명 | 주요 용도 |
| :--- | :--- | :--- |
| **Search API** | Kakao Book Search API | Daum 도서 검색을 통한 책 메타데이터(ISBN, 제목, 썸네일 등) 획득 |
| **OCR API** | Google Cloud Vision API | 업로드된 이미지 내 전체 텍스트 인식 및 텍스트 바운딩 박스(좌표) 추출 |
| **Storage** | AWS S3 | 사용자가 촬영한 원본 책 페이지 이미지 파일 영구 저장 |

### 2.3 Deployment & Infrastructure
- **Container:** Docker & Docker Compose (서버 환경 일관성 유지 및 간편한 배포)
- **Server:** AWS EC2 (Ubuntu, t2.micro 프리티어)
    - *Note:* Spring Boot와 MySQL 컨테이너 동시 구동을 위해 최소 2GB의 Swap 메모리 설정 필수.

---

## 3. Database Schema (ERD)
> Spring Data JPA Entity 설계 시 아래 스키마와 연관 관계(1:N, N:M)를 엄격히 준수할 것.

- **Users (사용자)**
    - `id` (PK, Long, Auto Increment)
    - `email` (String, Unique, 로그인 ID)
    - `password_hash` (String, BCrypt 해시)
    - `nickname` (String)
    - `created_at` (Timestamp)

- **Books (도서 메타데이터)**
    - `isbn` (PK, String)
    - `title` (String)
    - `author` (String)
    - `thumbnail_url` (String)
    - `publisher` (String)

- **User_Books (나의 서재 - Users와 Books의 연관 테이블)**
    - `id` (PK, Long)
    - `user_id` (FK -> Users.id)
    - `isbn` (FK -> Books.isbn)
    - `status` (Enum: READING, COMPLETED)
    - `added_at` (Timestamp)

- **Records (독서 기록 및 추출 문장)**
    - `id` (PK, Long)
    - `user_book_id` (FK -> User_Books.id)
    - `page_number` (Integer)
    - `image_url` (String, AWS S3 버킷 URL)
    - `extracted_text` (Text, 사용자가 최종 선택한 문장)
    - `created_at` (Timestamp)

---

## 4. Functional Requirements (주요 요구사항)

### 4.1 인증 및 회원 관리 (Auth)
- **자체 회원가입/로그인:** 외부 소셜 로그인 없이, 서비스 자체 DB 기반으로 이메일/비밀번호로 회원가입 및 로그인을 처리한다.
  - **회원가입**: 이메일/비밀번호/닉네임을 입력해 가입한다. 이메일은 중복될 수 없다.
  - **로그인**: 이메일/비밀번호로 로그인한다.
  - **토큰 발급**: 로그인 성공 시 서비스 자체 **JWT Access/Refresh Token**을 발급한다.
  - **토큰 인증**: `/api/auth/**`를 제외한 모든 API는 **Access Token**이 있어야 접근 가능하다.

### 4.2 도서 검색 및 내 서재 (Library)
- **도서 검색 프록시:** 프론트엔드에서 카카오 도서 검색 API를 직접 호출하지 않고, 백엔드(`/api/books/search`)를 거쳐 호출하여 카카오 REST API 키 노출을 방지한다.
- **서재 등록:** 검색된 책을 선택하면 `Books` 테이블에 저장(없을 경우)하고, `User_Books` 테이블에 매핑하여 내 서재에 추가한다.

### 4.3 문장 추출 (카메라 & OCR) - **핵심 기능**
- **이미지 촬영:** 내 서재의 특정 책에서 '기록 추가'를 누르면 기기 카메라가 실행된다.
- **이미지 처리 파이프라인:**
    1. 프론트에서 촬영된 이미지를 백엔드로 전송.
    2. 백엔드는 이미지를 AWS S3에 업로드하고 URL을 획득.
    3. 동시에 Google Cloud Vision API를 호출하여 이미지 내 텍스트의 문자열과 좌표 데이터를 추출.
    4. S3 URL과 OCR 결과(좌표 포함)를 프론트엔드로 반환.
- **인터랙티브 텍스트 선택 UI:** 프론트엔드는 반환받은 원본 사진 위에 OCR 텍스트 좌표를 반투명한 블록으로 오버레이한다. 사용자가 원하는 블록을 드래그하거나 터치하면 해당 텍스트들이 하단 입력창에 자동으로 결합(`extracted_text`)된다.

### 4.4 다이어리 및 캘린더 (Feed & Calendar)
- **도서별 피드:** 특정 책 상세 페이지 진입 시, 해당 책에 종속된 `Records` 리스트를 생성일(created_at) 기준 **내림차순**으로 정렬하여 인스타그램 피드처럼 보여준다. (페이지, 사진, 추출 문장 표시)
- **캘린더 뷰:** `react-native-calendars`를 활용하여 기록을 남긴 날짜에 마커(책갈피 아이콘 등)를 표시한다.

---

## 5. UI/UX Concept
- **Theme:** 아이보리/웜그레이 배경과 진한 텍스트 폰트를 사용하여 눈이 편안한 독서 환경 제공. 감성적인 명조체(Serif)를 포인트로 활용.
- **Navigation:** Bottom Tab Navigation (서재, 캘린더, 마이페이지) 구조.

---

## 6. Implementation Strategy for Cursor (작업 지시서)

> **Cursor AI에게:** 아래 Phase 순서대로 개발을 진행해 주십시오. 한 번에 하나의 Phase만 구현하고 리뷰를 요청하세요.

### Phase 1: 인프라 및 인증 셋업 (Backend & DB)
1. Spring Boot(Java) 프로젝트 및 Spring Security 셋업.
2. MySQL 8.0 구동을 위한 `docker-compose.yml` 작성.
3. `Users` 엔티티 및 이메일/비밀번호 기반 회원가입/로그인 처리 로직, JWT 발급 Service/Controller 구현.

### Phase 2: 도서 통합 (Backend & Frontend)
1. **Backend:** Kakao Book Search API 프록시 구현 (`RestTemplate` 또는 `WebClient` 사용). `Books`, `User_Books` 엔티티 매핑 및 내 서재 CRUD API 생성.
2. **Frontend:** Expo 초기화, React Navigation 라우팅, 자체 로그인/회원가입 UI 구현, 도서 검색 및 내 서재 UI 구현.

### Phase 3: 핵심 기능 (S3 & OCR)
1. **Backend:** AWS S3 이미지 업로드 유틸리티 개발. Google Vision API 연동 (`AnnotateImageRequest`). S3 URL 및 OCR 좌표 응답 API 작성.
2. **Frontend:** `expo-camera` 연동. 서버로부터 받은 OCR 좌표를 이미지 위에 렌더링하고, 터치/드래그 시 텍스트를 추출하는 UI/UX 구현.

### Phase 4: 피드 및 캘린더
1. **Backend:** `Records` 엔티티 CRUD 구현. 날짜별 월간/일간 기록 조회 API.
2. **Frontend:** 도서별 피드 뷰(내림차순 스크롤) 및 캘린더 마커 표시 UI 연동.