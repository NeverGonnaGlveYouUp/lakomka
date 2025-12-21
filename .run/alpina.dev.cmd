@echo off
REM Windows CMD version of alpina.dev.env

set POSTGRES_DB=postgres
set POSTGRES_HOST=192.168.2.219
set POSTGRES_USER=postgres
set POSTGRES_PASSWORD=

set S3_SCHEME=http
set S3_HOST=192.168.2.219
set S3_PORT=9000
set S3_ACCESS=minioadmin
set S3_SECRET=minioadmin
set S3_BUCKET=lakomka

set SPRING_PROFILES_ACTIVE=jpa-dev,debug,demo
REM set SPRING_PROFILES_ACTIVE=dev,debug,demo
set CAPTCHA_SECRET_KEY=

echo Environment variables set for alpina.dev
echo.
echo POSTGRES_DB=%POSTGRES_DB%
echo POSTGRES_HOST=%POSTGRES_HOST%
echo POSTGRES_USER=%POSTGRES_USER%
echo POSTGRES_PASSWORD=%POSTGRES_PASSWORD%
echo.
echo S3_SCHEME=%S3_SCHEME%
echo S3_HOST=%S3_HOST%
echo S3_PORT=%S3_PORT%
echo S3_ACCESS=%S3_ACCESS%
echo S3_SECRET=%S3_SECRET%
echo S3_BUCKET=%S3_BUCKET%
echo.
echo SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%
echo CAPTCHA_SECRET_KEY=%CAPTCHA_SECRET_KEY%
echo.

REM java -jar api/target/lakomka.jar
java -jar lakomka.jar
