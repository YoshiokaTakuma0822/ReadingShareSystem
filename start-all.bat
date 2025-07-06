@echo off

cd backend
echo === Starting Docker Compose ===
docker compose up -d

echo === Starting Spring Boot ===
start cmd /k mvnw.cmd spring-boot:run

cd ../frontend
echo === Starting Frontend Dev Server ===
start cmd /k npm run dev

cd ..
echo === All processes started ===
pause
