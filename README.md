"# MSA_Example01" 

# 1. 도커를 통한 db설치 및 비밀번호설정
docker run --name mysql-container -d -p 3306:3306 -e 
MYSQL_ROOT_PASSWORD=1234 mysql

# 2. 스키마 생성 
create database ordermsa;

# 3. redis설치
docker run --name redis-container -d -p 6379:6379 redis

# 4. rabbitmq설치
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management

