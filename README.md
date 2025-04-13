# 🛠️ kingkit

> 스타트업과 학생들을 위한, 실전형 **Spring 기반 백엔드 인프라 템플릿**  
> 누구나 5분 안에 실행 가능한 범용 백엔드 스타터 프로젝트

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/Yijungu/kingkit/terraform.yml)
![License](https://img.shields.io/github/license/Yijungu/kingkit)
![Stars](https://img.shields.io/github/stars/Yijungu/kingkit)

---

## 🚀 소개

**kingkit**은 인프라 고민 없이 바로 프로젝트 개발에 집중할 수 있도록 설계된  
Spring 기반의 백엔드 템플릿입니다.  
✅ Terraform 인프라 구성, ✅ CI/CD, ✅ RDS, ✅ 보안, ✅ 예제 API까지 모두 포함되어 있어  
**5분 안에 로컬 실행**이 가능합니다.

---

## ✨ 특징

- ⚙️ **Spring Boot 기반** 범용 백엔드 구조
- 🛠 **Terraform + GitHub Actions**로 자동 인프라 구성
- 🛡 **보안 그룹, IAM, VPC 등** 실무 수준 구성 포함
- 📦 예제 API (`todo-service`) 제공
- 💾 AWS RDS (PostgreSQL) 연동
- 🌍 dev / prod 환경 분리 가능
- 🚀 5분 설치, 1초 스타 가능

---

## 📦 포함된 구성 요소

| 구성 요소 | 설명 |
|-----------|------|
| `core/` | 인증, 유저, 에러 핸들링 |
| `infra/terraform/` | 인프라 코드 (S3, RDS, SG 등) |
| `deployment/` | Docker, GitHub Actions |
| `examples/todo-service/` | 예제 CRUD API |
| `docs/` | 프로젝트 사용 문서 |

---

## ⚡ 빠르게 시작하기

```bash
# 1. 레포 클론
git clone https://github.com/Yijungu/kingkit.git
cd kingkit

# 2. GitHub Secrets 설정
# (AWS 키, DB 정보 등)

# 3. 로컬 실행 (Spring Boot)
cd examples/todo-service
./gradlew bootRun
