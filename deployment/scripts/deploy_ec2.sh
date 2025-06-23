#!/bin/bash
set -euo pipefail

SERVICE_NAME="$1"
IMAGE_TAG="$2"
REGION="${REGION:-ap-northeast-2}"
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REPO_URI="$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$SERVICE_NAME"

aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"

docker pull "$REPO_URI:$IMAGE_TAG"
if [ $(docker ps -aq -f name=$SERVICE_NAME) ]; then
  docker stop "$SERVICE_NAME" && docker rm "$SERVICE_NAME"
fi

docker run -d --name "$SERVICE_NAME" -p 8080:8080 "$REPO_URI:$IMAGE_TAG"

