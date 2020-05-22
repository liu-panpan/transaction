# Project Description：
summary positions data by transaction data save.

# Environment： 
jdk1.8,maven3.6,H2,postgresql,IntelliJ IDEA 

# Technical Framework:
springBoot2.0

# Installation:
1. install: mvn clean install -Dmaven.test.skip=true
2. for dev test: mvn clean test -Pdev
3. for develop package: mvn clean package -Pdevelop -Dmaven.test.skip=true
# Database:
1. install postgresql version later version 9.4
2. postgresql database init sql:
````
-- create role
create role "trade" login encrypted password 'trade' inherit connection limit -1;
-- create database
create database trade with owner = trade encoding = 'UTF8';
-- login by trade, create schema
create schema trade authorization trade;
````
# Junit:
1. mvn clean test -Pdev
2. open test report file coverage:
//{$projectFolder}/target/site/jacoco/index.html
3. Note: {$projectFolder} is the workspace full path,use data-h2.sql for init data
4. use H2 data in browser path: 
http://localhost:8088/h2-console/

# Use Swagger2 API:
## path: http://localhost:8088/swagger-ui.html

