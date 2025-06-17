#!/bin/bash
# 모든 출력 → /var/log/user-data.log 로 동시에 저장 & 콘솔에도 표시
exec > >(tee /var/log/user-data.log | logger -t user-data -s 2>/dev/console) 2>&1
set -euxo pipefail         # e:에러시 즉시종료, u:미정변수오류, x:trace, o pipefail

echo "✅ [0] 시작: EC2 초기 설정 시작"

############################################
# 1. 시스템 업데이트 및 유틸 설치
############################################
echo "📦 [1] 시스템 업데이트 및 유틸 설치"
dnf update -y

# 이미 설치돼 있는 curl-minimal 과 충돌하지 않게 git·unzip만 설치
dnf install -y git unzip

# 혹시 ‘풀버전 curl’이 꼭 필요하면 minimal → full 로 스왑
# dnf swap curl-minimal curl -y || true

############################################
# 2. Nginx 설치
############################################
echo "🌐 [2] Nginx 설치"
dnf config-manager --set-enabled nginx1     # amazon-linux-extras 대신 dnf 사용
dnf install -y nginx
systemctl enable --now nginx

############################################
# 3. Docker & Java17 설치
############################################
echo "🐳 [3] Docker + Java 설치"
dnf install -y docker   java-17-amazon-corretto
systemctl enable --now docker
usermod -aG docker ec2-user
java -version

echo "✅ [4] EC2 초기화 완료"
exit 0
