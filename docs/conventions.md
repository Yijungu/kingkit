# 🧰 Kingkit 개발 컨벤션

이 문서는 `kingkit` 프로젝트의 모듈 간 코드 중복 최소화, 테스트 유틸리티 재사용성 확보를 위해  
지켜야 할 **공통 DTO 및 테스트 유틸 작성 규칙**을 정의합니다.

---

## 1. 내부 API 통신용 DTO는 `lib-dto`에 위치시킨다

### ✅ 목적
- `auth-service` ↔ `user-service` 등 마이크로서비스 간 내부 통신 시 사용되는 DTO들이 각 서비스에 중복 정의되지 않도록 관리

### ✅ 규칙
- 내부 서비스 간 사용되는 DTO는 반드시 `lib-dto` 모듈 내에 정의한다.
- **내부 전용 DTO**는 `UserInternalDto`, `AuthInternalDto` 등으로 명명하고, 외부 API와 명확히 구분한다.
- 모든 FeignClient, internal API 응답은 이 DTO를 참조해야 한다.

### 📁 디렉토리 구조 예시

lib-dto/
└── src/main/java/com/kingkit/lib_dto/
├── UserDto.java
└── ...

---

## 2. 공유 DTO 테스트용 Fixture는 `lib-test-support`에 작성한다

### ✅ 목적
- 여러 서비스에서 동일한 DTO 테스트 데이터를 생성하는 상황에서 **중복 없이 재사용** 가능하도록 함
- 테스트의 **일관성**과 **신뢰성** 확보

### ✅ 규칙
- DTO 테스트용 객체 생성을 위한 픽스처는 반드시 `lib-test-support` 모듈의 `fixture/` 하위에 작성한다.
- `UserFixture.sampleUser()` 등의 명명 패턴을 따른다.
- 각 픽스처 클래스는 하나의 도메인/DTO에 대해 담당하도록 구성한다.

### 📁 디렉토리 구조 예시

lib-test-support/
└── src/main/java/com/kingkit/testsupport/fixture/
├── UserFixture.java
└── ...


---

## 3. 테스트 유틸리티 클래스 관리 규칙

| 항목 | 규칙 |
|------|------|
| 요청 시뮬레이터 | `MockRequestBuilder` 등은 `util/` 디렉토리에 위치 |
| JWT 테스트 도우미 | `JwtTestUtils`, `TokenTestFactory` 등은 `util/`에 작성 |
| 커스텀 사용자 인증 어노테이션 | `@WithMockUserJwt` → `annotation/WithMockUserJwt.java` |
| 공통 테스트 설정 | 필요한 경우 `application-test.yml`을 `resources/`에 위치시켜 통합 관리 |

---

## 4. 모듈별 책임 명세

| 모듈명             | 책임 및 역할 |
|--------------------|------------------------------------------------|
| `lib-dto`          | 모든 내부 통신에 사용되는 **공통 DTO 정의** |
| `lib-test-support` | 테스트 전용 픽스처, 유틸리티, 어노테이션 등을 모은 **공통 테스트 도구 모음** |
| `user-service` 외  | 비즈니스 로직만 작성, DTO/Fixture 직접 정의 금지 (단, UI 응답 전용 DTO는 예외 가능) |

---

## 🔖 참고 예시

### Feign Client DTO 사용 예
```java
// ❌ 안됨: user-service에서 자체 정의
public class UserDto { ... }

// ✅ 이렇게 해야 함
import com.kingkit.lib_dto.UserDto;
