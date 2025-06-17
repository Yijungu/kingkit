#!/bin/bash
set -e

echo "✅ [0] 시작: EC2 초기 설정 시작"

# 1. 시스템 업데이트 및 유틸 설치
yum update -y
yum install -y git unzip curl

# 2. setup-nginx.sh 실행 내용 직접 포함
echo "🌐 [2] Nginx 설치"
amazon-linux-extras enable nginx1
yum install -y nginx
systemctl enable nginx
systemctl start nginx

# 3. setup-docker-java.sh 실행 내용 직접 포함
echo "🐳 [3] Docker + Java 설치"
yum install -y docker
systemctl enable docker
systemctl start docker
usermod -aG docker ec2-user

yum install -y java-17-amazon-corretto
java -version

echo "✅ [4] EC2 초기화 완료"
