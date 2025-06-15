# 🛠️ kingkit

> 스타트업과 학생들을 위한 실전형 **Spring 기반 백엔드 인프라 템플릿**  
> 누구나 5분 안에 실행 가능한 범용 백엔드 스타터 프로젝트

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/Yijungu/kingkit/ci.yml?label=CI)
![License](https://img.shields.io/github/license/Yijungu/kingkit)
![Stars](https://img.shields.io/github/stars/Yijungu/kingkit)

---

## 🚀 소개

**kingkit**은 인프라 고민 없이 **개발에만 집중**할 수 있도록 설계된 Spring 기반 백엔드 템플릿입니다.

- ✅ Terraform 기반 인프라 구성
- ✅ JWT 및 API-Key 인증 보안 구조 내장
- ✅ 공통 DTO 및 테스트 유틸 분리 관리
- ✅ GitHub Actions 기반 CI 파이프라인
- ✅ 실전형 예제 API 제공

**5분 안에 로컬 실행이 가능합니다.**

---

## ✨ 주요 특징

- ⚙️ **Spring Boot 기반 멀티모듈 구조**
- 🔒 **JWT + API-Key 보안 기능 내장**
- 📦 **공통 모듈화 구조** (`lib-dto`, `lib-security`, `lib-test-support`)
- 🛠 **Terraform + GitHub Actions** 인프라 자동화
- 💾 AWS RDS (PostgreSQL) 연동 및 환경 분리 (dev / prod)
- ✅ **테스트 커버리지 기준 설정** 및 검증

---

## 📁 디렉토리 구성

| 디렉토리            | 설명                                  |
|---------------------|---------------------------------------|
| `core/`             | 주요 도메인 서비스 (`auth`, `user`)  |
| `lib/`              | 공통 모듈 (`dto`, `security`, `test`) |
| `examples/`         | 예제 서비스 (`todo-service`)          |
| `infra/terraform/`  | AWS 인프라 코드                       |
| `deployment/`       | Docker, Helm, GitHub Actions 설정     |
| `monitoring/`       | Prometheus, Grafana, Alertmanager     |
| `docs/`             | 구조 및 규칙 문서                     |

---

## 🧩 멀티모듈 구조 예시

kingkit/
├── core/
│ ├── auth-service/
│ └── user-service/
├── lib/
│ ├── lib-dto/
│ ├── lib-security/
│ └── lib-test-support/
├── examples/
│ └── todo-service/
├── infra/
│ └── terraform/
├── deployment/
├── monitoring/
└── docs/


---

## 📚 개발 규칙

| 항목              | 규칙 설명                          |
|------------------|-----------------------------------|
| 내부 API DTO      | `lib-dto` 모듈에 정의             |
| 테스트 픽스처     | `lib-test-support/fixture/`       |
| 보안 유틸         | `lib-security/`에 구성 (JWT 등)  |
| 테스트 유틸       | `lib-test-support/util/`에 작성   |

---

## 📈 테스트 및 커버리지 기준

- ✅ **서비스별 단위 테스트** 완료 (`auth`, `user`, `filter`, `handler`, `controller`)
- ✅ **CI 연동:** 실패 시 리포트 아카이브
- ✅ **JaCoCo 커버리지 기준** 적용 가능

| 계층             | Instruction | Branch |
|------------------|-------------|--------|
| Service          | ≥ 90%       | ≥ 70%  |
| Filter / OAuth2  | ≥ 70%       | ≥ 60%  |
| **전체 목표**     | 실서비스 수준 유지 |

---

## 🖼 아키텍처 구성

| 구성 요소         | 설명                                                 |
|------------------|------------------------------------------------------|
| GitHub Actions   | 코드 변경 시 Terraform + 테스트 실행                |
| Terraform        | AWS 인프라 자동 구성 (RDS, S3, IAM 등)              |
| Docker / Helm    | 컨테이너 빌드 및 K8s 배포 지원                       |
| Spring Services  | `auth`, `user`, `gateway`, `config-server`           |
| Monitoring       | Prometheus + Grafana 구성                            |
| Database         | AWS RDS (PostgreSQL) 운영 대응                       |

---

## ⚡ 빠르게 시작하기


### 1. 레포지토리 클론
```bash
git clone https://github.com/Yijungu/kingkit.git
cd kingkit
```
### 2. GitHub Secrets 설정 (필수)
#### JWT_SECRET, DB 접속 정보, AWS 키 등 필요

### 3. 예제 API 실행

```bash
cd examples/todo-service
./gradlew bootRun
```
## 🧪 예제 테스트 실행

### 전체 테스트 실행
```bash
./gradlew clean build
```

### 특정 모듈 테스트 (예: user-service)
```bash
./gradlew :core:user-service:test
```
## 🤝 커뮤니티 및 기여
컨트리뷰션을 환영합니다!
PR 작성 전 docs/conventions.md를 꼭 확인해주세요.
실무 적용 가능한 구조로 함께 진화시켜 나가요.

## 📜 라이선스

MIT License © Yijungu