# 🧩 kingkit - user-service

Spring Boot 기반 사용자 서비스 모듈. 회원가입, 유저 조회 등의 기능을 담당하며, 인증/인가는 별도 auth-service에서 관리합니다.

---

## 📦 프로젝트 구조

```
core/user-service
├── src
│   ├── main
│   │   ├── java/com/kingkit/user_service
│   │   │   ├── controller          # REST API 엔드포인트
│   │   │   ├── service             # 비즈니스 로직
│   │   │   ├── repository          # JPA Repository
│   │   │   ├── domain              # 엔티티
│   │   │   ├── dto                 # 요청/응답 DTO
│   │   │   ├── config              # 설정
│   │   │   └── exception           # 전역 예외 처리
│   │   └── resources
│   │       └── application.yml     # 설정 파일
│   └── test
│       └── java/com/kingkit/user_service
│           ├── controller
│           ├── service
│           └── integration         # 통합 테스트
└── build.gradle.kts
```

---

## ⚙️ 실행 방법

### 1. 환경 구성
- Java 17
- Gradle 8.13+
- Spring Boot 3.2.4

### 2. DB 설정
- 운영용 PostgreSQL을 사용하며, Flyway로 마이그레이션 관리합니다.
- 테스트 환경은 H2 인메모리 DB로 분리되어 있습니다.

#### ✅ 로컬용 PostgreSQL 준비
```sql
-- 유저 및 DB 생성
CREATE USER user_service WITH PASSWORD 'your_password';
CREATE DATABASE userdb OWNER user_service;

-- 권한 부여
\c userdb
GRANT CONNECT ON DATABASE userdb TO user_service;
GRANT USAGE, CREATE ON SCHEMA public TO user_service;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO user_service;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO user_service;

-- 이후 생성될 객체에도 자동 권한 부여
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO user_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT ON SEQUENCES TO user_service;
```

---

## 🧪 테스트 환경

### ✅ 단위 테스트 (Junit + Mockito)
- `UserServiceTest`, `UserServiceImplTest`
- Mock 객체로 비즈니스 로직 검증

### ✅ 통합 테스트 (@SpringBootTest)
- `UserIntegrationTest`
- 실제 H2 DB를 사용해 HTTP 요청과 DB 저장을 검증

---

## 🔐 보안 처리
- Spring Security는 auth-service에서 담당함
- user-service는 CSRF만 끄고, 인증 없는 API만 허용

테스트 환경에서는 다음 설정으로 필터를 비활성화함:
```java
@AutoConfigureMockMvc(addFilters = false)
```

---

## 🧾 API 문서
- Springdoc OpenAPI + Swagger UI 연동
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`

```kotlin
// build.gradle.kts
implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
```

---

## 🧯 에러 처리
- `GlobalExceptionHandler` 에서 모든 예외 핸들링
- 공통 포맷: `ErrorResponse`

```json
{
  "status": 400,
  "message": "이미 사용 중인 이메일입니다"
}
```

---

## 🧪 테스트 DB 분리
`src/test/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
```

---

## 📍 앞으로 할 일

- [ ] **Auth-service 연동**
  - JWT 기반 인증 토큰 검증
  - 유저 정보 요청 시 인증 헤더 검증

- [ ] **API 응답 표준화**
  - 성공 응답도 `ApiResponse<T>`로 감싸기
  - 상태 코드, 메시지, 데이터 일관 구조 적용

- [ ] **회원 정보 수정/삭제 기능 추가**
  - 비밀번호 수정, 프로필 수정 등
  - 인증된 사용자만 가능하도록 제한

- [ ] **Swagger 문서 개선**
  - DTO 필드에 `@Schema` 설명 추가
  - API 설명 `@Operation(summary = "...")` 명확히 작성

- [ ] **테스트 케이스 강화**
  - 예외 상황 테스트 추가
  - Controller → Service → DB 흐름 전체 시나리오 검증

- [ ] **CI 연동**
  - GitHub Actions 기반 테스트 자동화
  - main 브랜치 push 시 테스트 및 빌드 확인

- [ ] **멀티 프로파일 구성**
  - `application-dev.yml`, `application-prod.yml`로 환경 분리
  - `@Profile` 활용해 dev/test/prod 로직 분기

- [ ] **모듈 간 API 문서 통합**
  - user-service와 auth-service의 OpenAPI 문서 통합 고려
  - gateway 레벨에서 문서 제공 가능성 검토

- [ ] **운영 모니터링 도입**
  - Actuator 기반 health 체크
  - Prometheus + Grafana 연동 가능성 탐색

---

## 🙋‍♂️ 문의
- Maintainer: IH King
- Contact: [your-email@domain.com]

