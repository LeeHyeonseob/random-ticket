#!/bin/bash

# 환경 설정
ENV=${1:-dev}

# 유효한 환경 확인
if [[ "$ENV" != "dev" && "$ENV" != "prod" ]]; then
  echo "유효하지 않은 환경: $ENV"
  echo "사용법: $0 [env]"
  echo "env: 실행 환경 (dev, prod) - 기본값: dev"
  exit 1
fi

# .env 파일 확인
if [[ ! -f .env ]]; then
  echo "ERROR: .env 파일이 없습니다."
  exit 1
fi

# Docker 빌드 캐시 정리 (에러가 있었던 경우)
if [[ "${CLEAN_BUILD}" == "true" ]]; then
  docker builder prune -a -f
fi

# 도커 이미지 빌드
docker-compose build --no-cache
if [ $? -ne 0 ]; then
  echo "ERROR: 도커 이미지 빌드에 실패했습니다."
  exit 1
fi

# 이전 컨테이너 정리
docker-compose down

# 컨테이너 시작
if [[ "$ENV" == "dev" ]]; then
  # 개발 환경: 기본 docker-compose.yml 사용
  docker-compose up -d
else
  # 운영 환경: docker-compose.prod.yml 추가
  docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
fi

if [ $? -ne 0 ]; then
  echo "ERROR: 컨테이너 시작에 실패했습니다."
  exit 1
fi

# 컨테이너 상태 표시
docker-compose ps
