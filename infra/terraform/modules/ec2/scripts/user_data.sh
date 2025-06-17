#!/bin/bash
set -euo pipefail
exec > >(tee -a /var/log/user-data.log) 2>&1

echo "✅ [0] 시작: EC2 초기 설정 시작"

############################################################
# 1. 시스템 업데이트 및 유틸 설치
############################################################
echo "📦 [1] 시스템 업데이트 및 유틸 설치"
dnf update -y
dnf install -y git unzip   # curl 설치 X → curl-minimal 그대로 사용

############################################################
# 2. Nginx 설치
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
usermod -aG docker ec2-user

dnf install -y java-17-amazon-corretto
java -version

echo "✅ [4] EC2 초기화 완료"
