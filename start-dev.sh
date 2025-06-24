#!/bin/bash

GREEN="\033[0;32m"
CYAN="\033[0;36m"
RED="\033[0;31m"
NC="\033[0m"

echo -e "${CYAN}🔧 1. Docker 인프라 시작 중...${NC}"
cd docker || { echo -e "${RED}docker 디렉토리 없음!${NC}"; exit 1; }
docker-compose up -d || { echo -e "${RED}Docker 실행 실패!${NC}"; exit 1; }
cd ..

echo -e "${CYAN}🚀 2. Spring Boot 백엔드 실행 중... (백그라운드)${NC}"
cd backend || { echo -e "${RED}backend 디렉토리 없음!${NC}"; exit 1; }
./gradlew bootRun > ../backend.log 2>&1 &
BACKEND_PID=$!
echo -e "${GREEN}✅ 백엔드 실행 완료 (PID: $BACKEND_PID)${NC}"
cd ..

echo -e "${CYAN}🌐 3. React 프론트엔드 실행 중... (백그라운드)${NC}"
cd frontend || { echo -e "${RED}frontend 디렉토리 없음!${NC}"; exit 1; }

if [ ! -d "node_modules" ]; then
    echo -e "${CYAN}📦 npm install 실행 중...${NC}"
    npm install
fi

npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo -e "${GREEN}✅ 프론트엔드 실행 완료 (PID: $FRONTEND_PID)${NC}"
cd ..

echo -e "${CYAN}📄 로그: tail -f backend.log frontend.log${NC}"

