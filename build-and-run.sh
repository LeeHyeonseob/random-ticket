#!/bin/bash

# 컬러 설정
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 환경 설정
ENV=${1:-dev}

# 사용법 표시
show_usage() {
  echo -e "${BLUE}사용법: $0 [env]${NC}"
  echo -e "  env: 실행 환경 (dev, prod) - 기본값: dev"
  echo -e "예: $0 prod"
}

# 도움말 표시
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  show_usage
  exit 0
fi

# 유효한 환경 확인
if [[ "$ENV" != "dev" && "$ENV" != "prod" ]]; then
  echo -e "${RED}유효하지 않은 환경: $ENV${NC}"
  show_usage
  exit 1
fi

echo -e "${YELLOW}===== 환경: ${ENV} =====${NC}"

# .env 파일 확인
if [[ ! -f .env ]]; then
  echo -e "${RED}ERROR: .env 파일이 없습니다. .env.example을 복사하여 .env 파일을 생성해주세요.${NC}"
  echo -e "${YELLOW}다음 명령어로 생성할 수 있습니다: cp .env.example .env${NC}"
  exit 1
fi

echo -e "${GREEN}.env 파일이 존재합니다.${NC}"

# MySQL init 디렉토리 확인 (prod 환경용)
if [[ "$ENV" == "prod" && ! -d "./docker/mysql/init" ]]; then
  echo -e "${YELLOW}MySQL 초기화 디렉토리가 없습니다. 생성합니다.${NC}"
  mkdir -p ./docker/mysql/init
fi

echo -e "${YELLOW}===== 도커 이미지 빌드 중 (${ENV} 환경) =====${NC}"
docker-compose build
if [ $? -ne 0 ]; then
  echo -e "${RED}ERROR: 도커 이미지 빌드에 실패했습니다.${NC}"
  exit 1
fi

echo -e "${YELLOW}===== 이전 컨테이너 정리 중 =====${NC}"
docker-compose down
if [ $? -ne 0 ]; then
  echo -e "${RED}WARNING: 이전 컨테이너 정리 중 문제가 발생했습니다. 계속 진행합니다.${NC}"
fi

echo -e "${YELLOW}===== 컨테이너 시작 중 (${ENV} 환경) =====${NC}"
if [[ "$ENV" == "dev" ]]; then
  # 개발 환경: 기본 docker-compose.yml 사용
  docker-compose up -d
else
  # 운영 환경: docker-compose.prod.yml 추가
  docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
fi

if [ $? -ne 0 ]; then
  echo -e "${RED}ERROR: 컨테이너 시작에 실패했습니다.${NC}"
  exit 1
fi

echo -e "${YELLOW}===== 컨테이너 상태 =====${NC}"
docker-compose ps

echo -e "${GREEN}===== 애플리케이션이 성공적으로 시작되었습니다 (${ENV} 환경) =====${NC}"
echo -e "${GREEN}로그를 확인하려면: docker-compose logs -f app${NC}"