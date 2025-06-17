#!/bin/bash
set -euo pipefail

# 모든 stdout/stderr 를 user-data.log 에 남김
exec > >(tee -a /var/log/user-data.log) 2>&1

echo "✅ [0] 시작: EC2 초기 설정 시작"

############################################################
# 1. 시스템 업데이트 및 유틸 설치
############################################################
echo "📦 [1] 시스템 업데이트 및 유틸 설치"
dnf update -y
# curl-minimal ↔ curl 충돌 예방: curl-minimal 제거
if rpm -q curl-minimal >/dev/null 2>&1; then
  dnf remove -y curl-minimal
fi
dnf install -y git unzip curl

############################################################
# 2. Nginx 설치 (AL2023은 extras 없음)
############################################################
echo "🌐 [2] Nginx 설치"
dnf install -y nginx
systemctl enable --now nginx

############################################################
# 3. Docker + Java 설치
############################################################
echo "🐳 [3] Docker + Java 설치"
dnf install -y docker
systemctl enable --now docker
usermod -aG docker ec2-user   # ec2-user 도커 권한 부여

dnf install -y java-17-amazon-corretto
java -version

echo "✅ [4] EC2 초기화 완료"
