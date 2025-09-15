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

# 5. 각 프로젝트 내용
### frist : 내부 통신을 RestTemplate 이용
### second : 내부 통신을 feign 및 kafka 이용
### third : config-server를 통해 마이크로서비스의 환경설정(yml)을 중앙(github)에서 관리
### application.yml : thrid에서 공통 설정 파일
### member-service.yml : thrid에서 member server에서 사용하는 설정 파일
### ordering-service.yml : thrid에서 ordering server에서 사용하는 설정 파일
### product-service.yml : thrid에서 product server에서 사용하는 설정 파일
##### 출처 : 인프런 : 빠르게 배우는 Spring Cloud 기초(MSA)

