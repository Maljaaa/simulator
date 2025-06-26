#!/bin/bash

RED="\033[0;31m"
GREEN="\033[0;32m"
NC="\033[0m"

echo -e "${RED}🛑 백엔드와 프론트엔드 프로세스 종료 중...${NC}"

# backend bootRun 프로세스 종료
BACKEND_PIDS=$(ps aux | grep '[b]ootRun' | awk '{print $2}')
if [ -n "$BACKEND_PIDS" ]; then
  echo "백엔드 PID: $BACKEND_PIDS"
  kill $BACKEND_PIDS
  echo -e "${GREEN}✅ 백엔드 종료 완료${NC}"
else
  echo "백엔드 프로세스가 없습니다."
fi

# frontend vite dev 프로세스 종료
FRONTEND_PIDS=$(ps aux | grep '[v]ite' | awk '{print $2}')
if [ -n "$FRONTEND_PIDS" ]; then
  echo "프론트엔드 PID: $FRONTEND_PIDS"
  kill $FRONTEND_PIDS
  echo -e "${GREEN}✅ 프론트엔드 종료 완료${NC}"
else
  echo "프론트엔드 프로세스가 없습니다."
fi

echo -e "${RED}🐳 Docker 컨테이너 중지 중...${NC}"
cd docker || { echo "docker 디렉토리 없음!"; exit 1; }
docker-compose down

echo -e "${GREEN}🎉 모든 서비스 종료 완료!${NC}"

