# CoachLink

**CoachLink**는 스포츠 강사와 수강생을 연결하는 강좌 매칭 플랫폼입니다.  
사용자는 원하는 종목과 강좌를 탐색해 수강 신청할 수 있고, 선수 회원은 자신의 종목을 기반으로 강좌를 개설하고 관리할 수 있습니다.  
또한 수강생은 강좌에 대한 리뷰와 이미지를 등록하여 다른 사용자에게 강좌 경험을 공유할 수 있습니다.

---

## 주요 기능(기여도: 상: ⭐ / 중: ★ / 하: ☆)

✅ *개발 완료*

| 기능명 | 설명 | 기여도 |
|--------|------|--------|
| **일반 / 선수 회원가입** | 일반 회원과 선수 회원을 구분하여 가입하고, 선수 회원은 종목을 선택하도록 구a성 | 상 ⭐ |
| **회원정보 수정 및 탈퇴** | 마이페이지에서 회원정보 수정, 비밀번호 변경, 회원 탈퇴 기능 제공 | 상 ⭐ |
| **프로필 이미지 관리** | 사용자 프로필 이미지 등록, 조회, 수정, 삭제 기능 제공 | 중 ★ |
| **강좌 등록 및 탐색** | 선수 회원이 강좌를 등록하고, 사용자는 강좌 목록과 상세 정보를 조회 | 중 ★ |
| **내 강좌 관리** | 본인이 등록한 강좌의 취소 및 삭제 기능 제공 | 중 ★ |
| **수강 신청 기능** | 사용자가 강좌를 신청하고 신청 상태를 확인할 수 있도록 구현 | 중 ★ |
| **리뷰 CRUD** | 리뷰 작성, 조회, 수정, 삭제 및 리뷰 이미지 관리 기능 구현 | 상 ⭐ |
| **알림 기능** | 강좌 수정/취소 시 수강 신청자에게 알림 제공 | 중 ★ |

---

## 사용 기술 스택

- 🎨 Frontend<br>
<img src="https://img.shields.io/badge/React-61DAFB?style=flat&logo=react&logoColor=black" height="25" /> <img src="https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black" height="25" /> <img src="https://img.shields.io/badge/React%20Router-CA4245?style=flat&logo=reactrouter&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/Tailwind%20CSS-06B6D4?style=flat&logo=tailwindcss&logoColor=white" height="25" />

- 🔧 Backend<br>
<img src="https://img.shields.io/badge/Java-17-007396?style=flat&logo=openjdk&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=springboot&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=springsecurity&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/JPA-59666C?style=flat&logo=hibernate&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white" height="25" />

- 🗃 Database<br>
<img src="https://img.shields.io/badge/MariaDB-003545?style=flat&logo=mariadb&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/AWS%20RDS-527FFF?style=flat&logo=amazonrds&logoColor=white" height="25" />

- ☁️ Infra<br>
<img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=flat&logo=amazonec2&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/CloudFront-FF9900?style=flat&logo=amazonaws&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/Vercel-000000?style=flat&logo=vercel&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white" height="25" />

- 🔗 API & External<br>
<img src="https://img.shields.io/badge/Google%20OAuth-4285F4?style=flat&logo=google&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/Daum%20Postcode-FFCD00?style=flat&logo=kakaotalk&logoColor=black" height="25" /> <img src="https://img.shields.io/badge/Public%20Facility%20API-0064FF?style=flat&logo=data&logoColor=white" height="25" />

- 🛠 개발 도구 & 빌드<br>
<img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/npm-CB3837?style=flat&logo=npm&logoColor=white" height="25" /> <img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white" height="25" />

---

## Architecture

```text
[ React / Vite Frontend ]
          |
          | HTTPS API Request
          v
[ CloudFront ]
          |
          v
[ Spring Boot Backend ]
[ Docker Container on EC2 ]
          |
          | Spring Data JPA
          v
[ MariaDB on AWS RDS ]
```

- 프론트엔드는 React/Vite 기반으로 구현하고 Vercel에 배포했습니다.
- 백엔드는 Spring Boot 기반 REST API 서버로 구현하고 EC2 Docker 컨테이너에서 실행했습니다.
- 프론트엔드와 백엔드는 CloudFront를 통해 HTTPS 기반으로 통신하도록 구성했습니다.
- 회원 인증은 JWT 기반으로 처리했습니다.
- 회원, 강좌, 리뷰, 이미지 데이터는 MariaDB RDS에 저장했습니다.

---

## ERD

> ERD 이미지를 추가할 예정입니다.

<!--
![ERD](이미지 URL)![image.png](attachment:94bc8d66-d8e9-4936-9162-d3b1c05ca192:image.png)
-->

---

## 주요 화면 설명

### 메인 페이지 <br><br>
<!-- ![메인 페이지](이미지 URL) -->
등록된 강좌 목록을 확인할 수 있는 메인 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 강좌명, 강사명, 장소, 종목 정보를 확인할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 강좌 상태에 따라 모집 중, 확정, 취소, 종료 상태를 구분해 표시합니다.<br><br><br>

### 로그인 페이지 <br><br>
<!-- ![로그인 페이지](이미지 URL) -->
일반 로그인과 Google OAuth 로그인을 제공하는 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 로그인 성공 시 JWT 토큰을 발급받아 로컬 스토리지에 저장합니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 이후 인증이 필요한 API 요청에 Authorization 헤더를 포함합니다.<br><br><br>

### 회원가입 페이지 <br><br>
<!-- ![회원가입 페이지](이미지 URL) -->
일반 회원과 선수 회원으로 가입할 수 있는 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 아이디 중복 확인을 통해 사용 가능한 아이디인지 검증합니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- Daum 주소 API를 활용해 주소를 입력할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 선수 회원 가입 시 종목 선택을 필수로 처리합니다.<br><br><br>

### 종목 선택 팝업 <br><br>
<!-- ![종목 선택 팝업](이미지 URL) -->
선수 회원 가입 시 종목을 선택하는 팝업 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 백엔드의 종목 목록 API를 호출해 종목 데이터를 조회합니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 선택한 종목의 sportId를 회원가입 요청 데이터에 포함합니다.<br><br><br>

### 마이페이지 <br><br>
<!-- ![마이페이지](이미지 URL) -->
로그인한 사용자가 자신의 정보를 관리할 수 있는 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 회원정보 조회 및 수정 기능을 제공합니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 비밀번호 변경과 회원 탈퇴 기능을 제공합니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 선수 회원은 내 강좌 관리 탭에서 강좌를 관리할 수 있습니다.<br><br><br>

### 내 강좌 관리 페이지 <br><br>
<!-- ![내 강좌 관리](이미지 URL) -->
선수 회원이 본인이 등록한 강좌를 관리하는 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 등록한 강좌의 상태를 확인할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 강좌를 취소 상태로 변경할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 모든 상태의 강좌를 삭제할 수 있습니다.<br><br><br>

### 강좌 상세 페이지 <br><br>
<!-- ![강좌 상세 페이지](이미지 URL) -->
강좌의 상세 정보를 확인하는 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 강좌명, 강사명, 종목, 장소, 일정, 모집 인원을 확인할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 로그인 사용자는 수강 신청 상태에 따라 신청 또는 취소할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 강좌에 등록된 리뷰 목록을 확인할 수 있습니다.<br><br><br>

### 강좌 등록 페이지 <br><br>
<!-- ![강좌 등록 페이지](이미지 URL) -->
선수 회원이 새로운 강좌를 개설하는 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 강좌명, 소개, 시설명, 일정, 모집 인원, 이미지를 등록할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 공공 시설 API를 활용해 시설 정보를 검색하고 위치 좌표를 저장합니다.<br><br><br>

### 리뷰 작성 및 수정 페이지 <br><br>
<!-- ![리뷰 작성](이미지 URL) -->
수강생이 강좌에 대한 리뷰를 작성하고 수정하는 화면입니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 평점과 리뷰 내용을 입력할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 리뷰 이미지를 함께 업로드할 수 있습니다.<br>
&nbsp;&nbsp;&nbsp;&nbsp;- 리뷰 수정 시 기존 이미지 유지, 새 이미지 추가, 이미지 삭제를 처리합니다.<br><br><br>

---

## 담당 파트

### 회원가입

회원가입 기능의 프론트엔드와 백엔드를 구현했습니다.

- 일반 회원과 선수 회원 역할 구분
- 아이디 중복 확인 API 연동
- 주소 검색 기능 연동
- 선수 회원 가입 시 종목 선택 필수 검증
- 회원 권한을 `MEMBER`, `PLAYER`로 분리
- 비밀번호 BCrypt 암호화 저장

선수 회원은 강좌를 등록할 수 있는 사용자이므로, 일반 회원과 달리 가입 단계에서 종목 정보가 필요했습니다.  
이를 위해 종목 선택 팝업을 구현하고 선택된 `sportId`를 회원가입 요청 데이터에 포함하도록 처리했습니다.

```text
선수회원 선택
→ 종목 선택 팝업
→ 종목 목록 조회
→ 종목 선택
→ sportId 포함 회원가입 요청
```

---

### 회원정보 관리

로그인한 사용자가 자신의 정보를 관리할 수 있도록 마이페이지 기능을 구현했습니다.

- 회원정보 조회
- 이름, 이메일, 주소, 성별 수정
- 비밀번호 변경
- 프로필 이미지 등록 및 수정
- 회원 탈퇴

아이디는 로그인 식별자이자 회원 PK로 사용되므로 수정 대상에서 제외하고, 변경 가능한 정보만 수정되도록 처리했습니다.

---

### 회원 탈퇴

회원 탈퇴 시 연관 데이터까지 고려한 삭제 로직을 구현했습니다.

- 수강 신청 내역 삭제
- 작성 리뷰 삭제
- 리뷰 이미지 삭제
- 회원 삭제

회원이 리뷰와 수강 신청 데이터에 연결되어 있기 때문에 단순 회원 삭제 시 데이터 무결성 문제가 발생할 수 있었습니다.  
이를 해결하기 위해 연관 데이터를 먼저 정리한 뒤 회원을 삭제하도록 구현했습니다.

```text
수강 신청 내역 삭제
→ 리뷰 이미지 삭제
→ 리뷰 삭제
→ 회원 삭제
```

---

### 리뷰 CRUD

강좌에 대한 리뷰 기능의 전체 흐름을 구현했습니다.

- 리뷰 작성
- 강좌별 리뷰 목록 조회
- 리뷰 상세 조회
- 리뷰 수정
- 리뷰 삭제
- 리뷰 이미지 업로드 및 삭제

리뷰 작성 시 평점과 텍스트뿐 아니라 이미지 파일도 함께 등록할 수 있도록 `multipart/form-data` 방식으로 요청을 구성했습니다.

```text
username
lectureId
rating
content
files
```

---

### 리뷰 이미지 관리

리뷰 이미지 업로드 및 수정 기능을 구현했습니다.

- 리뷰 작성 시 이미지 첨부
- 리뷰 수정 시 이미지 추가
- 기존 이미지 삭제
- 리뷰 삭제 시 관련 이미지 정리

리뷰 수정 시 모든 이미지를 다시 저장하지 않고, 삭제할 이미지 ID 목록을 별도로 전달해 필요한 이미지만 제거하도록 구현했습니다.

---

## 문제 해결 및 배운 점

### 1. 선수 회원 가입 시 종목 데이터 누락 문제

#### Problem
선수 회원은 강좌 등록 시 종목 정보가 필요하지만, 일반 회원과 동일한 회원가입 흐름만 사용하면 종목이 선택되지 않은 상태로 가입될 수 있었습니다.

#### Solution
- 회원가입 폼에서 `PLAYER` 역할 선택 시 종목 선택을 필수로 검증했습니다.
- 별도 종목 선택 팝업을 구현했습니다.
- 선택된 종목의 `sportId`를 회원가입 요청 데이터에 포함했습니다.

#### Result
선수 회원 가입 시 강좌 등록에 필요한 종목 데이터가 누락되지 않도록 개선했습니다.

#### Learned
사용자 유형에 따라 필요한 데이터가 달라질 수 있으므로, 역할별 필수 데이터와 도메인 조건을 화면과 서버 양쪽에서 검증해야 한다는 점을 배웠습니다.

---

### 2. 회원 탈퇴 시 연관 데이터 충돌 문제

#### Problem
회원은 수강 신청, 리뷰, 리뷰 이미지와 연결되어 있어 회원 데이터만 바로 삭제하면 외래키 제약이나 참조 데이터 문제가 발생할 수 있었습니다.

#### Solution
회원 탈퇴 로직에서 연관 데이터를 먼저 정리한 후 회원을 삭제하도록 순서를 구성했습니다.

```text
수강 신청 삭제
→ 리뷰 이미지 삭제
→ 리뷰 삭제
→ 회원 삭제
```

#### Result
회원 탈퇴 과정에서 연관 데이터 충돌 없이 정상 삭제가 가능하도록 개선했습니다.

#### Learned
CRUD 중 삭제는 단순히 한 행을 제거하는 작업이 아니라, 데이터 관계와 무결성을 고려해 삭제 범위와 순서를 설계해야 하는 작업임을 배웠습니다.

---

### 3. 비밀번호 보안 처리 문제

#### Problem
회원가입과 비밀번호 변경 기능에서 비밀번호를 그대로 저장하거나 단순 비교하면 보안상 위험이 있었습니다.

#### Solution
- 회원가입 시 비밀번호를 BCrypt로 암호화해 저장했습니다.
- 비밀번호 변경 시 기존 비밀번호는 암호화 비교를 통해 검증했습니다.
- 새 비밀번호를 다시 암호화해 저장했습니다.

#### Result
회원 인증 정보가 평문으로 저장되지 않도록 처리했습니다.

#### Learned
회원 기능은 화면 구현보다 인증 정보의 안전한 저장과 검증 방식이 더 중요하며, 보안 로직은 서비스 초기 단계부터 고려해야 한다는 점을 배웠습니다.

---

### 4. 리뷰 수정 시 이미지 관리 문제

#### Problem
리뷰 수정에서 이미지 추가와 삭제가 함께 발생할 수 있어 단순한 CRUD보다 상태 관리가 복잡했습니다.  
모든 이미지를 매번 다시 저장하면 불필요한 데이터 갱신과 중복 처리 문제가 생길 수 있었습니다.

#### Solution
- 새 이미지 파일과 삭제할 이미지 ID 목록을 함께 전달했습니다.
- 백엔드에서는 삭제 대상 이미지만 제거했습니다.
- 새 이미지만 추가 저장하도록 구현했습니다.

#### Result
리뷰 텍스트와 이미지 변경을 함께 처리하면서도 이미지 데이터 관리가 명확해졌습니다.

#### Learned
파일이 포함된 CRUD는 텍스트 데이터보다 변경 상태를 세밀하게 나눠야 하며, 추가/유지/삭제 상태를 명확히 구분하는 API 설계가 중요하다는 점을 배웠습니다.

---

### 5. 배포 환경에서 API 연결 문제

#### Problem
로컬에서는 정상 동작하던 기능이 배포 환경에서는 CORS, JWT 환경변수, RDS SSL 설정, CloudFront API 경유 설정 차이로 정상 연결되지 않는 문제가 발생했습니다.

#### Solution
- 백엔드 CORS 허용 origin에 Vercel 도메인을 반영했습니다.
- CloudFront를 통해 HTTPS API 통신을 구성했습니다.
- RDS SSL 연결을 위한 JDBC 옵션을 반영했습니다.
- JWT Secret, OAuth 환경변수 등 배포 환경변수를 점검했습니다.
- 프론트엔드 API Base URL을 배포 환경에 맞게 설정했습니다.

#### Result
배포 환경에서도 프론트엔드, 백엔드, DB가 정상적으로 연결되도록 구성했습니다.

#### Learned
서비스 기능은 로컬 구현만으로 끝나지 않으며, 배포 환경의 네트워크, 보안 설정, 환경변수까지 포함해야 실제로 동작하는 기능이 된다는 점을 배웠습니다.

---

## 실행 방법

### Frontend

```bash
cd frontend/portfolio-front
npm install
npm run dev
```

### Backend

```bash
cd backend/portfolio-back
./gradlew bootRun
```

### Docker Compose

```bash
docker compose up --build
```

---

## 배포 환경

| 구분 | 주소 |
|------|------|
| Frontend | Vercel 배포 주소 |
| Backend API | CloudFront 배포 주소 |
| Database | AWS RDS MariaDB |

---

## Summary

CoachLink 프로젝트에서 회원과 리뷰 기능의 프론트엔드 및 백엔드 개발을 담당했습니다.  
단순 CRUD 구현을 넘어 권한 분리, 입력 검증, 비밀번호 보안, 파일 업로드, 연관 데이터 삭제, 배포 환경 연동 문제를 직접 경험하고 해결했습니다.
